package donghyuk.pcshop.controller;

import donghyuk.pcshop.dto.OrderDto;
import donghyuk.pcshop.dto.OrderHistoryDto;
import donghyuk.pcshop.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
import java.util.Optional;

@Controller                     // 스프링 컨트롤러 등록
@RequiredArgsConstructor        // OrderService를 생성자를 통해 의존성 주입
public class OrderController {
    private final OrderService orderService;

    // 상품 주문 (POST - 저장)
    @PostMapping(value = "/order")
    @ResponseBody
    public ResponseEntity order(@RequestBody @Valid OrderDto orderDto,
                                BindingResult bindingResult, Principal principal) {
        // 에러 발생시 에러 및 필드 정보를 반환.
        if (bindingResult.hasErrors()) {
            StringBuilder sb = new StringBuilder();
            List<FieldError> fieldErrors = bindingResult.getFieldErrors();
            for (FieldError fieldError : fieldErrors) {
                sb.append(fieldError.getDefaultMessage());
            }
            return new ResponseEntity<String>(sb.toString(), HttpStatus.BAD_REQUEST);
        }
        // 주문자(이메일정보)로 전달받은 주문 저장하는 서비스 호출.
        Long orderId;
        try {
            orderId = orderService.order(orderDto, principal.getName());
        } catch (Exception e) {
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<Long>(orderId, HttpStatus.OK);
    }

    // 주문 이력 조회
    // {page}로 조회하는 페이지 정보 전달
    @GetMapping(value = {"/orders", "/orders/{page}"})
    public String orderHistory(@PathVariable(name = "page") Optional<Integer> page, Principal principal, Model model) {
        // 페이지 객체 생성, 페이지 정보없으면 0, 페이지 사이즈는 5
        Pageable pageable = PageRequest.of(page.isPresent() ? page.get() : 0, 5);
        // 로그인 사용자의 주문 이력 리스트 조회하여 DTO에 담는다.
        Page<OrderHistoryDto> orderHistoryDtos = orderService.getOrderList(principal.getName(), pageable);
        // 주문 이력 리스트, 페이지 등을 HTML 렌더링에 사용할 정보로 추가.
        model.addAttribute("orders", orderHistoryDtos);
        model.addAttribute("page", pageable.getPageNumber());
        model.addAttribute("maxPage", 10);
        // HTML - order/orderHistory.html
        return "order/orderHistory";
    }

    // 주문 취소 (POST - 저장, 주문 상태를 Cancel로 변경)
    @PostMapping(value = "/order/{orderId}/cancel") // 취소할 주문 정보를 {orderId}로 전달받음.
    @ResponseBody
    public ResponseEntity orderCancel(@PathVariable(name = "orderId") Long orderId, Principal principal) {
        // 취소할 주문이 로그인 사용자의 것인지 확인, 없으면 에러.
        if (!orderService.validateOrder(orderId, principal.getName())) {
            return new ResponseEntity<String>("주문을 취소할 권한이 없습니다!", HttpStatus.FORBIDDEN);
        }
        // 주문의 상태를 취소로 변경.
        orderService.orderCancel(orderId);
        return new ResponseEntity<Long>(orderId, HttpStatus.OK);
    }
}
