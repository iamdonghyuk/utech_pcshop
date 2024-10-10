package donghyuk.pcshop.repository;

import donghyuk.pcshop.dto.ItemSearchDto;
import donghyuk.pcshop.dto.MainItemDto;
import donghyuk.pcshop.entity.Item;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

// ItemRepositoryCustom 인터페이스
public interface ItemRepositoryCustom {
    // 관리자가 상품 조회한 결과를 페이지 객체로 반환하는 멤버 함수. 결과는 Item 객체
    Page<Item> getAdminItemPage(ItemSearchDto itemSearchDto, Pageable pageable);

    // 일반회원이 상품조회한 결과를 페이지 객체로 반환. 결과는 MainItemDto
    Page<MainItemDto> getMainItemPage(ItemSearchDto itemSearchDto, Pageable pageable);
}
