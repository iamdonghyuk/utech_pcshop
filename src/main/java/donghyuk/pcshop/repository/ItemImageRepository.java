package donghyuk.pcshop.repository;

import donghyuk.pcshop.entity.ItemImage;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

// 상품이미지 리포트지토리
public interface ItemImageRepository extends JpaRepository<ItemImage, Long> {
    // 상품ID로 상품의 이미지를 조회, 결과는 이미지 ID Asc 순서로.
    List<ItemImage> findByItemIdOrderByIdAsc(Long itemId);
    // 상품ID로 상품의 대표 이미지를 찾는다. (isMainImage = 'Y')
    ItemImage findByItemIdAndIsMainImage(Long itemId, String isMainImage);
}
