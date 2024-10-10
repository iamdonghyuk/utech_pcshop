package donghyuk.pcshop.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import javax.persistence.*;

// 장바구니 엔티티
@Entity
@Table(name = "cart")
@Getter @Setter @ToString
public class Cart extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "cart_id")
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)   // 조회 지연 로딩
    @JoinColumn(name = "member_id")
    private Member member;

    // 회원의 장바구니를 생성하고 정보를 반환.
    public static Cart createCart(Member member) {
        Cart cart = new Cart();
        cart.setMember(member);
        return cart;
    }
}
