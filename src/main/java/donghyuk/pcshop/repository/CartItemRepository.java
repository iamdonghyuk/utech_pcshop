package donghyuk.pcshop.repository;

import donghyuk.pcshop.dto.CartListDto;
import donghyuk.pcshop.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

// 장바구니 조회 리포지토리
public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    // 장바구니ID와 상품ID로 장바구니에 상품이 담겨 있는지 조회하는 함수
    CartItem findByCartIdAndItemId(Long cartId, Long itemId);

    // 쿼리DSL을 이용하여 장바구니ID로 장바구니에 담겨 있는 상품 정보, 가격, 수량, 이미지 정보를 조회하여
    // CartListDto 리스트로 반환한다.
    @Query("select new donghyuk.pcshop.dto.CartListDto(ci.id, i.itemName, i.price, ci.count, im.imageUrl) " +
            "from CartItem ci, ItemImage im " +
            "join ci.item i " +
            "where ci.cart.id = :cartId " +
            "and im.item.id = ci.item.id " +
            "and im.isMainImage = 'Y' " +
            "order by ci.createdTime desc")
    List<CartListDto> findCartListDto(@Param("cartId") Long cartId);
}

