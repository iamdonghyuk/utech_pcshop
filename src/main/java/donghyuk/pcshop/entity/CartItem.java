package donghyuk.pcshop.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

// 장바구니 항목 엔티티
@Getter @Setter @Entity
@Table(name = "cart_item")
public class CartItem extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "cart_item_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)  // 조회 지연 로딩
    @JoinColumn(name = "cart_id")
    private Cart cart;

    @ManyToOne(fetch = FetchType.LAZY)  // 조회 지연 로딩
    @JoinColumn(name = "item_id")
    private Item item;                  // 주문할 상품

    private int count;                  // 주문 갯수

    // 장바구니 항목을 추가, 상품과 주문 갯수를 전달 받음.
    public static CartItem createCartItem(Cart cart, Item item, int count) {
        CartItem cartItem = new CartItem();
        cartItem.setCart(cart);
        cartItem.setItem(item);
        cartItem.setCount(count);
        return cartItem;
    }

    // 장바구니 상품항목의 주문 갯수를 추가
    public void addCount(int count) {
        this.count += count;
    }

    // 장바구니 상품항목의 주문 갯수를 변경
    public void updateCount(int count) {
        this.count = count;
    }
}
