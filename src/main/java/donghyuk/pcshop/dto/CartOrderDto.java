package donghyuk.pcshop.dto;

import lombok.Getter;
import lombok.Setter;
import java.util.List;

// 장바구니 주문 Dto
@Getter @Setter     // getter & setter 자동 생성
public class CartOrderDto {
    private Long cartItemId;
    private List<CartOrderDto> cartOrderDtoList;
}
