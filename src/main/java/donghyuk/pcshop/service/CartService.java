package donghyuk.pcshop.service;

import donghyuk.pcshop.dto.CartItemDto;
import donghyuk.pcshop.dto.CartListDto;
import donghyuk.pcshop.dto.CartOrderDto;
import donghyuk.pcshop.dto.OrderDto;
import donghyuk.pcshop.entity.Cart;
import donghyuk.pcshop.entity.CartItem;
import donghyuk.pcshop.entity.Item;
import donghyuk.pcshop.entity.Member;
import donghyuk.pcshop.repository.CartItemRepository;
import donghyuk.pcshop.repository.CartRepository;
import donghyuk.pcshop.repository.ItemRepository;
import donghyuk.pcshop.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.util.StringUtils;

import javax.persistence.EntityNotFoundException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

@Service                    // 스프링 서비스 등록
@Transactional              // 서비스내 DB트랜잭션이 보장되도록 보장. 변경을 위한 snapshot 저장.
@RequiredArgsConstructor    // private static으로 선언된 멤버변수들을 생성자를 통해 의존성 주입받는다.
public class CartService {
    // 주입받은 리포지토리 및 서비스 멤버 변수들
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final MemberRepository memberRepository;
    private final ItemRepository itemRepository;
    private final OrderService orderService;

    // 장바구니 담기
    // 이메일 주소로 회원정보 조회후 장바구니에 주문할 상품을 추가. (장바구니없으면 생성)
    public Long addCart(CartItemDto cartItemDto, String email) {
        // 이메일로 회원 정보 조회.
        Member member = memberRepository.findByEmail(email);
        // 회원ID로 장바구니 조회.
        Cart cart = cartRepository.findByMemberId(member.getId());
        // 장바구니가 없으면 새로 생성, 있으면 조회된 것 사용.
        if (cart == null) {
            cart = Cart.createCart(member);
            cartRepository.save(cart);  // 생성된 장바구니 정보를 DB에 저장한다.
        }
        // 입력받은 장바구니상품ID로 상품을 조회. 없으면 엔티티 에러 발생시킴.
        Item item = itemRepository.findById(cartItemDto.getItemId()).orElseThrow(EntityNotFoundException::new);
        // 조회된 상품ID가 장바구니에 있는지 조회.
        CartItem cartItem = cartItemRepository.findByCartIdAndItemId(cart.getId(), item.getId());
        if (cartItem == null) {
            // 상품이 장바구니에 없으면 생성후 추가
            cartItem = CartItem.createCartItem(cart, item, cartItemDto.getCount());
            cartItemRepository.save(cartItem);      // DB 저장.
        } else {
            // 상품이 장바구니에 이미 존재한다면 수량 증가 (DB 저장 포함)
            cartItem.addCount(cartItemDto.getCount());
        }
        // 장바구니 상품ID를 반환.
        return cartItem.getId();
    }

    // 장바구니 조회
    @Transactional(readOnly = true) // 조회용, snapshot을 저장하지 않는다. 성능 향상.
    public List<CartListDto> getCartList(String email) {
        // 장바구니 리스트 Dto 객체 생성
        List<CartListDto> cartListDtos = new ArrayList<>();
        // 이메일로 회원 정보 조회.
        Member member = memberRepository.findByEmail(email);
        // 조회된 회원ID로 장바구니 조회.
        Cart cart = cartRepository.findByMemberId(member.getId());
        // 회원 ID로 장바구니가 없으면 앞에서 생성한 신규 장바구니 리스트 Dto 반환.
        if (cart == null) {
            return cartListDtos;
        }
        // 장바구니 ID로 장바구니 상품을 조회, 반환.
        cartListDtos = cartItemRepository.findCartListDto(cart.getId());
        return cartListDtos;
    }

    // 현재 로그인한 사용자가 장바구니의 주인인지 확인하는 함수.
    @Transactional(readOnly = true)  // 조회용, snapshot을 저장하지 않는다. 성능 향상.
    public boolean validateCartItem(Long cartItemId, String email) {
        // 이메일로 회원 정보 조회.
        Member currentMember = memberRepository.findByEmail(email);
        // 장바구니 상품ID로 장바구니 조회, 없으면 엔티티 에러 발생 시킴.
        CartItem cartItem = cartItemRepository.findById(cartItemId).orElseThrow(EntityNotFoundException::new);
        // 장바구니 상품으로 장바구니를 조회, 해당 장바구니의 회원 정보를 가져온다.
        Member savedMember = cartItem.getCart().getMember();
        // 장바구니 소유자와 로그인 계정의 이메일이 동일한지 체크한다.
        if (StringUtils.equals(currentMember.getEmail(), savedMember.getEmail())) {
            return true;
        }
        return false;
    }

    // 장바구니 상품 수량 변경
    public void updateCartItemCount(Long cartItemId, int count) {
        // 장바구니 상품을 조회, 없으면 엔티티 에러.
        CartItem cartItem = cartItemRepository.findById(cartItemId).orElseThrow(EntityNotFoundException::new);
        // 수량을 DB에서 업데이트.
        cartItem.updateCount(count);
    }

    // 장바구니 상품 삭제
    public void deleteCartItem(Long cartItemId) {
        // 장바구니 상품을 조회, 없으면 엔티티 에러.
        CartItem cartItem = cartItemRepository.findById(cartItemId).orElseThrow(EntityNotFoundException::new);
        // 장바구니 상품을 DB에서 삭제.
        cartItemRepository.delete(cartItem);
    }

    // 장바구니 상품들을 주문
    public Long orderCartItem(List<CartOrderDto> cartOrderDtoList, String email) {
        // 주문 DTO 객체 생성
        List<OrderDto> orderDtoList = new ArrayList<>();
        for (CartOrderDto cartOrderDto : cartOrderDtoList) {
            // 장바구니 상품을 장바구니주문에서 조회. 없으면 에러 발생 시킴.
            CartItem cartItem = cartItemRepository.findById(cartOrderDto.getCartItemId()).orElseThrow(EntityNotFoundException::new);
            // 주문 DTO 객체 생성
            OrderDto orderDto = new OrderDto();
            orderDto.setItemId(cartItem.getItem().getId()); // 주문 DTO에 장바구니 상품 추가
            orderDto.setCount(cartItem.getCount());         // 주문 DTO에 장바구니 상품 갯수 추가.
            orderDtoList.add(orderDto);                     // 주문 DTO리스트에 주문 DTO 추가
        }
        // 회원(이메일)의 주문 생성.
        Long orderId = orderService.orders(orderDtoList, email);
        // 주문이 완료되었기 때문에 장바구니의 상품들은 제거한다.
        for (CartOrderDto cartOrderDto : cartOrderDtoList) {
            // orElseThrow 를 통해 값의 존재 여부 확인
            CartItem cartItem = cartItemRepository.findById(cartOrderDto.getCartItemId()).orElseThrow(EntityNotFoundException::new);
            cartItemRepository.delete(cartItem);
        }
        return orderId;
    }
}
