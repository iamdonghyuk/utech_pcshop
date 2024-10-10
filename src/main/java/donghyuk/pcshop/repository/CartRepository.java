package donghyuk.pcshop.repository;

import donghyuk.pcshop.entity.Cart;
import org.springframework.data.jpa.repository.JpaRepository;

// 장바구니 리포트지토리
public interface CartRepository extends JpaRepository<Cart,Long> {
    // 회원ID로 장바구니를 조회.
    Cart findByMemberId(Long memberId);
}
