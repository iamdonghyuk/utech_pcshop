package donghyuk.pcshop.service;

import donghyuk.pcshop.dto.OrderDto;
import donghyuk.pcshop.dto.OrderHistoryDto;
import donghyuk.pcshop.dto.OrderItemDto;
import donghyuk.pcshop.entity.*;
import donghyuk.pcshop.repository.ItemImageRepository;
import donghyuk.pcshop.repository.ItemRepository;
import donghyuk.pcshop.repository.MemberRepository;
import donghyuk.pcshop.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.util.StringUtils;

import javax.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.List;

@Service                        // 스프링 서비스 등록
@Transactional                  // DB 트랜잭션 보장
@RequiredArgsConstructor        // final 멤버 변수들을 의존성 주입.
public class OrderService {
    private final ItemRepository itemRepository;        // 상품을 불러와서 재고를 변경해야함
    private final MemberRepository memberRepository;    // 멤버를 불러와서 연결해야함
    private final OrderRepository orderRepository;      // 주문 객체를 저장해야함
    private final ItemImageRepository itemImageRepository;  // 상품 대표 이미지를 출력해야함

    // 회원 정보(이메일)로 상품 주문
    public Long order(OrderDto orderDto, String email) {
        // OrderItem(List) 객체 생성
        List<OrderItem> orderItemList = new ArrayList<>();
        Item item = itemRepository.findById(orderDto.getItemId()).orElseThrow(EntityNotFoundException::new);
        orderItemList.add(OrderItem.createOrderItem(item, orderDto.getCount()));
        // Order 객체 생성
        Member member = memberRepository.findByEmail(email);
        Order order =  Order.createOrder(member, orderItemList);
        // Order 객체 DB 저장 (Cascade로 인해 OrderItem 객체도 같이 저장)
        orderRepository.save(order);    // DB 저장
        return order.getId();
    }

    // 회원 정보(이메일)로 주문 내역 조회
    @Transactional(readOnly = true)
    public Page<OrderHistoryDto> getOrderList(String email, Pageable pageable) {
        // 이메일로 주문 리스트를 조회.
        List<Order> orders = orderRepository.findOrders(email, pageable);
        // 해당 계정으로 주문한 갯수
        Long totalCount = orderRepository.countOrder(email);

        // 주문 이력 Dto 리스트 객체 생성하여 조회된 주문 리스트를 하나씩 담는다.
        List<OrderHistoryDto> orderHistDtos = new ArrayList<>();
        for (Order order : orders) {
            OrderHistoryDto orderHistDto = new OrderHistoryDto(order);
            List<OrderItem> orderItems = order.getOrderItems();
            for (OrderItem orderItem : orderItems) {
                ItemImage itemImage = itemImageRepository.findByItemIdAndIsMainImage(orderItem.getItem().getId(), "Y");
                OrderItemDto orderItemDto = new OrderItemDto(orderItem, itemImage.getImageUrl());
                orderHistDto.addOrderItemDto(orderItemDto);
            }
            orderHistDtos.add(orderHistDto);
        }
        // 주문이력Dto 리스트와 총주문횟수를 페이지 객체로 반환.
        return new PageImpl<>(orderHistDtos, pageable, totalCount);
    }

    // 주문한 유저가 맞는지 검증
    @Transactional(readOnly = true)
    public boolean validateOrder(Long orderId, String email) {
        // 주문ID가 있는지 확인.
        Order order = orderRepository.findById(orderId).orElseThrow(EntityNotFoundException::new);
        // 조회된 주문의 회원이메일과 전달받은 이메일이 동일한지 확인.
        if (StringUtils.equals(order.getMember().getEmail(), email)) {
            return true;
        }
        return false;
    }

    // 주문 취소
    public void orderCancel(Long orderId) {
        // 주문ID가 있는지 확인.
        Order order = orderRepository.findById(orderId).orElseThrow(EntityNotFoundException::new);
        // 주문이 있으면 주문 취소.
        order.orderCancel();
    }

    // 장바구니 상품(들) 주문
    public Long orders(List<OrderDto> orderDtoList, String email) {
        // 전달받은 이메일로 회원 조회
        Member member = memberRepository.findByEmail(email);
        // 주문상품 리스트 객체 생성, 전달받은 주문DTO리스트를 옮겨 담는다.
        List<OrderItem> orderItemList = new ArrayList<>();
        for (OrderDto orderDto : orderDtoList) {
            Item item = itemRepository.findById(orderDto.getItemId()).orElseThrow(EntityNotFoundException::new);
            OrderItem orderItem = OrderItem.createOrderItem(item, orderDto.getCount());
            orderItemList.add(orderItem);
        }

        // 주문 상품 리스트와 회원 정보로 주문을 생성한다.
        Order order = Order.createOrder(member, orderItemList);
        orderRepository.save(order);        // DB 저장.
        return order.getId();
    }
}
