package donghyuk.pcshop.dto;

import donghyuk.pcshop.constant.ItemStatus;
import donghyuk.pcshop.entity.Item;
import lombok.Getter;
import lombok.Setter;
import org.modelmapper.ModelMapper;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

// 상품 등록/수정 DTO
@Getter @Setter         // getter & setter 자동 생성
public class ItemFormDto {
    private Long id;

    @NotBlank(message = "상품명은 필수 입력 값입니다.")
    private String itemName;

    @NotNull(message = "가격은 필수 입력 값입니다.")
    private Integer price;

    @NotBlank(message = "상품 상세는 필수 입력 값입니다.")
    private String itemDetail;

    @NotNull(message = "재고는 필수 입력 값입니다.")
    private Integer stock;

    private ItemStatus itemStatus;      // 상품 상태 (in stock, sold out)

    // 상품의 이미지 리스트 정보를 담을 멤버 변수.
    private List<ItemImageDto> itemImageDtoList = new ArrayList<>(); // 이미지 파일의 위치 정보 리스트
    private List<Long> itemImageIds = new ArrayList<>();             // 이미지 ID 리스트

    private static ModelMapper modelMapper = new ModelMapper();

    // View Data(DTO) -> Model Entity 상품 등록
    public Item createItem(){
        return modelMapper.map(this, Item.class);
    }

    // Model Entity -> View Data (DTO) DB에 값을 불러와 화면에 표시
    public static ItemFormDto of(Item item){
        return modelMapper.map(item, ItemFormDto.class);
    }
}
