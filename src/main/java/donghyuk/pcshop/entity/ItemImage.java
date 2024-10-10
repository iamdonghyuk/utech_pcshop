package donghyuk.pcshop.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

// 상품의 이미지 정보를 저장하는 클래스
@Entity
@Table(name = "item_image")
@Getter @Setter
public class ItemImage extends BaseEntity {
    @Id
    @Column(name="item_image_id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String imageName;         //이미지 파일명
    private String oriImageName;      //원본 이미지 파일명
    private String imageUrl;          //이미지 조회 경로
    private String isMainImage;       //대표 이미지 여부

    @ManyToOne(fetch = FetchType.LAZY)  // 조회 지연 로딩
    @JoinColumn(name = "item_id")
    private Item item;

    // 상품의 이미지 정보를 업데이트하는 함수
    public void updateItemImage(String oriImageName, String imageName, String imageUrl) {
        this.oriImageName = oriImageName;
        this.imageName = imageName;
        this.imageUrl = imageUrl;
    }
}