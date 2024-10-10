package donghyuk.pcshop.dto;

import donghyuk.pcshop.constant.ItemStatus;
import lombok.Getter;
import lombok.Setter;

// 상품 검색에 사용할 Dto
@Getter @Setter     // getter & setter 자동 생성
public class ItemSearchDto {
    private String searchDateType;          // 검색 날짜
    private ItemStatus searchItemStatus;    // 상품 재고 상태
    private String searchBy;                // 상품 등록자
    private String searchQuery = "";        // 쿼리 (HTML)
}
