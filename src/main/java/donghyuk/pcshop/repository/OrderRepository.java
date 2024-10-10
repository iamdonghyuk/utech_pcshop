package donghyuk.pcshop.repository;

import donghyuk.pcshop.entity.Order;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

// 주문리포트지토리
public interface OrderRepository extends JpaRepository<Order, Long> {
    // 회원의 이메일로 주문 정보를 조회하는 쿼리, 결과는 주문 리스트로 반환.
    @Query("select o from Order o " +
            "where o.member.email = :email " +
            "order by o.orderDate desc")
    List<Order> findOrders(@Param("email") String email, Pageable pageable);

    // 회원의 이메일로 주문 갯수를 조회하는 쿼리, 결과는 주문 갯수 (Long)
    @Query("select count(o) from Order o " +
            "where o.member.email = :email")
    Long countOrder(@Param("email") String email);
}
