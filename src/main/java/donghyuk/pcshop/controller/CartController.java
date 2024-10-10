package donghyuk.pcshop.controller;

import donghyuk.pcshop.dto.CartItemDto;
import donghyuk.pcshop.dto.CartListDto;
import donghyuk.pcshop.dto.CartOrderDto;
import donghyuk.pcshop.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.security.Principal;
import java.util.List;

@Controller                         // 스프링 컨트롤러 등록
@RequiredArgsConstructor            // 생성자에 CartService 주입
public class CartController {
    private final CartService cartService;

    // 장바구니 생성 및 상품 담기
    @PostMapping(value = "/cart")   // cart-POST (저장)
    @ResponseBody                   // 자바객체 CartItemDto와 HttpResponse의 ResponseBody와 매핑
    public ResponseEntity cart(@RequestBody @Valid CartItemDto cartItemDto,
                               BindingResult bindingResult, Principal principal) {
        // 타임리프에서 가져온 데이터를 자바 객체로 연결,
        // 매핑 에러 발생시 에러가 발생한 필드 정보를 에러로 반환
        if (bindingResult.hasErrors()) {
            StringBuilder sb = new StringBuilder();
            List<FieldError> fieldErrors = bindingResult.getFieldErrors();
            for (FieldError fieldError : fieldErrors) {
                sb.append(fieldError.getDefaultMessage());
            }
            return new ResponseEntity<String>(sb.toString(), HttpStatus.BAD_REQUEST);
        }

        Long cartItemId;
        try {
            // 장바구니에 상품을 추가하는 addCart 함수 호출. (신규 장바구니이면 장바구니를 새로 생성함)
            cartItemId = cartService.addCart(cartItemDto, principal.getName());
        } catch (Exception e) {
            // 에러 발생시 에러 메시지 반환
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
        // 에러 없을 경우 추가된 장바구니 아이템 ID 정보를 반환함.
        return new ResponseEntity<Long>(cartItemId, HttpStatus.OK);
    }


    // 장바구니 조회
    @GetMapping(value = "/cart")        // cart-GET (조회)
    public String cartList(Principal principal, Model model) {
        // 로그인 사용자의 이메일 주소를 이용해, 사용자의 장바구니를 조회한다.
        List<CartListDto> cartListDtos = cartService.getCartList(principal.getName());
        model.addAttribute("cartItems", cartListDtos);
        // HTML은 cart/cartList.html
        return "cart/cartList";
    }

    // 장바구니 내용 변경
    // PATCH는 수정명시한 데이터만 수정, PUT은 수정명시한 데이트 수정되고 나머지는 빈 값.
    // 여기서는 PATCH를 사용한다.
    @PatchMapping(value = "/cartItem/{cartItemId}")  // 수정할 아이템을 {cartItemId}로 받는다.
    @ResponseBody
    public ResponseEntity updateCartItem(@PathVariable(name = "cartItemId") Long cartItemId,
                                         int count, Principal principal) {
        if (count <= 0) {
            // 상품 수량이 0이하이면 에러.
            return new ResponseEntity<String>("상품을 1개 이상 담아주세요!", HttpStatus.BAD_REQUEST);
        } else if (!cartService.validateCartItem(cartItemId, principal.getName())) {
            // 로그인 사용자가 장바구니 소유한 계정인지 검증하여 아니면 에러.
            return new ResponseEntity<String>("수정할 권한이 없습니다.", HttpStatus.FORBIDDEN);
        }
        // 검증 통과후 장바구니 내용을 수정한다.
        cartService.updateCartItemCount(cartItemId, count);
        return new ResponseEntity<Long>(cartItemId, HttpStatus.OK);
    }

    // 장바구니 내용 삭제
    @DeleteMapping(value = "/cartItem/{cartItemId}")   // 삭제할 아이템을 {cartItemId}로 받는다.
    @ResponseBody
    public ResponseEntity deleteCartItem(@PathVariable(name = "cartItemId") Long cartItemId, Principal principal) {
        // 로그인 사용자가 장바구니 소유한 계정인지 검증하여 아니면 에러.
        if (!cartService.validateCartItem(cartItemId, principal.getName())) {
            return new ResponseEntity<String>("삭제할 권한이 없습니다.", HttpStatus.FORBIDDEN);
        }
        // 장바구니에서 아이템을 삭제한다.
        cartService.deleteCartItem(cartItemId);
        return new ResponseEntity<Long>(cartItemId, HttpStatus.OK);
    }

    // 장바구니에 있는 상품들을 주문.
    @PostMapping(value = "/cart/orders")
    @ResponseBody
    public ResponseEntity orders(@RequestBody CartOrderDto cartOrderDto, Principal principal) {
        // 장바구니 목록을 cartOrderDto 리스트에 할당
        List<CartOrderDto> cartOrderDtoList = cartOrderDto.getCartOrderDtoList();
        // 장바구니가 비어있거나 null일 경우에는 에러. BAD_REQUEST- 부적절한 요청
        if (cartOrderDtoList == null || cartOrderDtoList.size() == 0) {
            return new ResponseEntity<String>("주문할 상품을 선택해주세요!", HttpStatus.BAD_REQUEST);
        }
        // 장바구니 주문 상품들이 로그인 사용자가 추가한 것인지 검증.
        for (CartOrderDto cartOrderDto1 : cartOrderDtoList) {
            // FORBIDDEN - 권한이 없는 사용자를 알림
            if (!cartService.validateCartItem(cartOrderDto1.getCartItemId(), principal.getName())) {
                return new ResponseEntity<String>("주문할 권한이 없습니다!", HttpStatus.FORBIDDEN);
            }
        }
        // 검증후 장바구니 내용을 주문하고 orderId를 반환한다.
        Long orderId = cartService.orderCartItem(cartOrderDtoList, principal.getName());
        return new ResponseEntity<Long>(orderId, HttpStatus.OK);
    }
}
