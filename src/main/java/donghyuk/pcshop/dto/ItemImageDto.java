package donghyuk.pcshop.dto;

import donghyuk.pcshop.entity.ItemImage;
import lombok.Getter;
import lombok.Setter;
import org.modelmapper.ModelMapper;

// 상품 이미지 정보 Dto
@Getter @Setter     // getter & setter 자동 생성
public class ItemImageDto {
    private Long id;
    private String imageName;           // 이미지 파일이름
    private String oriImageName;        // 이미지 파일이름 원본
    private String imageUrl;            // 이미지 파일의 URL
    private String isMainImage;         // 상품의 대표이미지 여부 (Y/N)

    private static ModelMapper modelMapper = new ModelMapper();

    // Model Entity -> View Data (DTO)
    public static ItemImageDto of(ItemImage itemImage) {
        return modelMapper.map(itemImage, ItemImageDto.class);
    }
}
