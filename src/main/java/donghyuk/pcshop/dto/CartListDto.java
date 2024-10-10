package donghyuk.pcshop.dto;

import lombok.Getter;
import lombok.Setter;

// 장바구니 상품리스트 Dto
@Getter @Setter     // getter & setter 자동 생성
public class CartListDto {
    private Long cartItemId;        // CartItemID
    private String itemName;        // 상품명
    private int price;              // 상품가격
    private int count;              // 장바구니
    private String imageUrl;        // 상품이미지 URL

    public CartListDto(Long cartItemId, String itemName, int price, int count, String imageUrl){
        this.cartItemId = cartItemId;
        this.itemName = itemName;
        this.price = price;
        this.count = count;
        this.imageUrl = imageUrl;
    }
}
