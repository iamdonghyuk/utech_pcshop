package donghyuk.pcshop.dto;

import donghyuk.pcshop.constant.OrderStatus;
import donghyuk.pcshop.entity.Order;
import lombok.Getter;
import lombok.Setter;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

// 주문 이력 Dto
@Getter @Setter // getter & setter 자동 생성
public class OrderHistoryDto {
    private Long orderId;   // 주문 취소에 이용됨
    private String orderDate;
    private OrderStatus orderStatus;
    private List<OrderItemDto> orderItemDtoList = new ArrayList<>();

    // 오더 정보를 받아와 OrderHistory를 생성한다.
    public OrderHistoryDto(Order order) {
        this.orderId = order.getId();
        this.orderDate = order.getOrderDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
        this.orderStatus = order.getOrderStatus();
    }

    // 생성된 주문 이력에 주문 항목들을 추가한다.
    public void addOrderItemDto(OrderItemDto orderItemDto) {
        orderItemDtoList.add(orderItemDto);
    }
}
