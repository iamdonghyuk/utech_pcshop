package donghyuk.pcshop.repository;

import donghyuk.pcshop.entity.Item;
import org.springframework.data.jpa.repository.JpaRepository;

// 상품 리포트지토리, ItemRepositoryCustom 인터페이스를 상속받는다.
public interface ItemRepository extends JpaRepository<Item,Long>, ItemRepositoryCustom {
}
