package donghyuk.pcshop.dto;

import donghyuk.pcshop.entity.OrderItem;
import lombok.Getter;
import lombok.Setter;

// 주문 항목 Dto
@Getter @Setter // getter & setter 자동 생성
public class OrderItemDto {
    private String itemName;
    private int count;
    private int orderPrice;
    private String imageUrl;

    // 주문 항목과 상품의 이미지 URL로 DTO 생성.
    public OrderItemDto(OrderItem orderItem, String imageUrl) {
        this.itemName = orderItem.getItem().getItemName();
        this.count = orderItem.getCount();
        this.orderPrice = orderItem.getOrderPrice();
        this.imageUrl = imageUrl;
    }
}
