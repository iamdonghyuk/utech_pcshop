package donghyuk.pcshop.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;
import lombok.Setter;

// 메인 홈페이지의 상품 페이지 Dto
@Getter @Setter         // getter & setter 자동 생성
public class MainItemDto {
    private Long id;
    private String itemName;
    private String itemDetail;
    private String imageUrl;
    private Integer price;

    // QMainItemDto
    @QueryProjection  // QueryDSL 사용하여 Select할 때 사용 (ItemRepositoryCustomImpl.getMainItemPage)
    public MainItemDto(Long id, String itemName, String itemDetail,
                       String imageUrl, Integer price){
        this.id = id;
        this.itemName = itemName;
        this.itemDetail = itemDetail;
        this.imageUrl = imageUrl;
        this.price = price;
    }
}