package donghyuk.pcshop.repository;

import donghyuk.pcshop.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

// 주문 상품 리포지토리
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
}
