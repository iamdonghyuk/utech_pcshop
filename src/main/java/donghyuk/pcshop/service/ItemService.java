package donghyuk.pcshop.service;

import donghyuk.pcshop.dto.ItemFormDto;
import donghyuk.pcshop.dto.ItemImageDto;
import donghyuk.pcshop.dto.ItemSearchDto;
import donghyuk.pcshop.dto.MainItemDto;
import donghyuk.pcshop.entity.Item;
import donghyuk.pcshop.entity.ItemImage;
import donghyuk.pcshop.repository.ItemImageRepository;
import donghyuk.pcshop.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

// 상품 저장 & 수정 서비스
@Service                    // 스프링 서비스 등록
@RequiredArgsConstructor    // final 멤버변수를 생성자 의존성 주입
@Transactional              // DB 트랜잭션 보장
public class ItemService {
    private final ItemRepository itemRepository;
    private final ItemImageService itemImageService;
    private final ItemImageRepository itemImageRepository;

    // 상품 등록
    public Long saveItem(ItemFormDto itemFormDto, List<MultipartFile> itemImgFileList) throws Exception {
        // 상품 등록 (1번)
        Item item = itemFormDto.createItem();
        itemRepository.save(item);

        // 상품 이미지 등록(2번)
        for (int i = 0; i < itemImgFileList.size(); i++) {
            ItemImage itemimg = new ItemImage();
            itemimg.setItem(item);
            if (i == 0) {   // 첫번째를 대표이미지로 설정한다.
                itemimg.setIsMainImage("Y");
            } else {
                itemimg.setIsMainImage("N");
            }
            itemImageService.saveItemImage(itemimg, itemImgFileList.get(i));
        }
        return item.getId();
    }

    // 관리자 - 상품 조회
    @Transactional(readOnly = true)
    public Page<Item> getAdminItemPage(ItemSearchDto itemSearchDto, Pageable pageable) {
        return itemRepository.getAdminItemPage(itemSearchDto, pageable);
    }

    // 일반 회원 - 상품 조회
    @Transactional(readOnly = true)
    public ItemFormDto getItemDetail(Long itemId) {
        // 상품 이미지 엔티티들을 itemImgDto 객체로 변환하기 위해 itemImgDtoList 에 담음 (Model -> View)
        List<ItemImage> itemImageList = itemImageRepository.findByItemIdOrderByIdAsc(itemId);
        List<ItemImageDto> itemImageDtoList = new ArrayList<>();
        for (ItemImage itemImage : itemImageList) {
            ItemImageDto itemImgDto = ItemImageDto.of(itemImage);
            itemImageDtoList.add(itemImgDto);
        }

        // 상품 엔티티를 ItemFormDto 객체로 변환하기 위해 ItemFormDto에 담고, ItemImageDtoList도 추가한다.
        Item item = itemRepository.findById(itemId).orElseThrow(EntityNotFoundException::new);
        ItemFormDto itemFormDto = ItemFormDto.of(item);
        itemFormDto.setItemImageDtoList(itemImageDtoList);
        return itemFormDto;
    }

    // 상품 수정
    public Long updateItem(ItemFormDto itemFormDto, List<MultipartFile> itemImgFileList) throws IOException {
        // 입력 받은 상품 정보로 상품을 조회하고 상품 정보를 수정
        Item item = itemRepository.findById(itemFormDto.getId()).orElseThrow(EntityNotFoundException::new);
        item.updateItem(itemFormDto);
        // 상품의 이미지도 수정해준다.
        List<Long> itemImageIds = itemFormDto.getItemImageIds();
        for (int i = 0; i < itemImgFileList.size(); i++) {
            itemImageService.updateItemImage(itemImageIds.get(i), itemImgFileList.get(i));
        }
        return item.getId();
    }

    // 메인 홈페이지 상품 목록 조회
    @Transactional(readOnly = true)
    public Page<MainItemDto> getMainItemPage(ItemSearchDto itemSearchDto, Pageable pageable){
        return itemRepository.getMainItemPage(itemSearchDto, pageable);
    }
}
