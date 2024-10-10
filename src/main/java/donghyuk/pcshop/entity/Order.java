package donghyuk.pcshop.entity;


import donghyuk.pcshop.constant.OrderStatus;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

// 주문 엔티티 (order는 DB 예약어이기 때문에 테이블명을 orders로 한다.)
@Entity
@Getter @Setter
@Table(name = "orders")
public class Order extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "order_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)      // 조회 지연 로딩
    @JoinColumn(name = "member_id")
    private Member member;                  // 주문의 회원 정보

    private LocalDateTime orderDate;        // 주문일

    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus;        // 주문상태 (order / cancel)

    // order_item 테이블의 order 필드에 매핑
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<OrderItem> orderItems = new ArrayList<>(); // 주문의 상품 리스트

    // 주문에 상품을 추가하는 함수
    public void addOrderItem(OrderItem orderItem) {
        orderItems.add(orderItem);
        orderItem.setOrder(this);
    }

    // 주문을 생성하는 함수
    public static Order createOrder(Member member, List<OrderItem> orderItemList) {
        // 신규 주문 인스턴스 생성
        Order order = new Order();
        order.setMember(member);                        // 오더 인스턴스에 로그인 사용자를 할당
        // 주문에 주문상품을 추가.
        for (OrderItem orderItem : orderItemList) {
            order.addOrderItem(orderItem);
        }
        order.setOrderDate(LocalDateTime.now());        // 주문일 설정 (현재)
        order.setOrderStatus(OrderStatus.ORDER);        // 주문상태 설정 (주문)
        return order;
    }

    // 주문에 담겨있는 상품들의 총 금액을 계산하는 함수
    public int getTotalPrice() {
        int totalPrice = 0;
        for (OrderItem orderItem : orderItems) {
            totalPrice += orderItem.getTotalPrice();
        }
        return totalPrice;
    }

    // 주문 취소 함수, 주문의 상태를 CANCEL로 업데이트한다.
    public void orderCancel() {
        this.orderStatus = OrderStatus.CANCEL;      // 주문의 상태를 CANCEL로 변경
        for (OrderItem orderItem : orderItems) {
            orderItem.cancel();                     // 오더 아이템의 수량을 재고로 돌려준다.
        }
    }
}
