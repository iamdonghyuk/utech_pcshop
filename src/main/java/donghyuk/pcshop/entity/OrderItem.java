package donghyuk.pcshop.entity;

import lombok.Getter;
import lombok.Setter;
import javax.persistence.*;

@Entity
@Table(name = "order_item")
@Getter @Setter
public class OrderItem extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "order_item_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="item_id")
    private Item item;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Order order;

    private int orderPrice;             // 주문가격
    private int count;                  // 수량

    // 오더에 신규 상품을 추가하는 함수
    public static OrderItem createOrderItem(Item item, int count) {
        OrderItem orderItem = new OrderItem();
        orderItem.setItem(item);                    // 상품을 추가
        orderItem.setCount(count);                  // 상품 갯수 추가
        orderItem.setOrderPrice(item.getPrice());    // 상품의 가격 추가

        item.minusStock(count);                     // 상품의 재고 수량을 추가된 갯수만큼 빼준다.
        return orderItem;
    }

    // 상품의 단위 가격과 주문 수량을 곱하여 해당 상품의 주문 금액을 계산하는 함수
    public int getTotalPrice() {
        return this.orderPrice * this.count;
    }

    // 주문이 취소되었을 때 상품의 재고 수량을 돌려주는 함수.
    public void cancel() {
        this.getItem().addStock(count);
    }
}
