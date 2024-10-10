package donghyuk.pcshop.entity;

import donghyuk.pcshop.constant.ItemStatus;
import donghyuk.pcshop.dto.ItemFormDto;
import donghyuk.pcshop.exception.OutOfStockException;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;

// 상품 엔티티
@Entity
@Table(name = "item")
@Getter @Setter @ToString
public class Item extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "item_id")
    private Long id;

    @Column(nullable = false, length = 50)
    private String itemName;            // 상품명

    @Lob
    @Column(nullable = false)
    private String itemDetail;          // 상품 세부 설명

    @Column(nullable = false)
    private int price;                  // 상품 가격

    @Column(nullable = false)
    private int stock;                  // 상품의 현재 재고수량

    @Enumerated(EnumType.STRING)
    private ItemStatus itemStatus;      // 상품의 현재 재고상태 (in_stock, sold_out)

    // View에서 전달받은 정보 (DTO)로 상품 엔티티를 업데이트
    public void updateItem(ItemFormDto itemFormDto) {
        this.itemName = itemFormDto.getItemName();
        this.price = itemFormDto.getPrice();
        this.stock = itemFormDto.getStock();
        this.itemDetail = itemFormDto.getItemDetail();
        this.itemStatus = itemFormDto.getItemStatus();
    }

    // 재고 수량을 감소시키는 함수
    public void minusStock(int stock) {
        int remainStock = this.stock - stock;
        if (remainStock < 0) {
            throw new OutOfStockException("상품의 재고가 부족합니다. (현재 재고 수량: " + this.stock + ")");
        }
        this.stock = remainStock;
    }

    // 재고 수량을 증가시키는 함수
    public void addStock(int stock) {
        this.stock += stock;
    }
}
