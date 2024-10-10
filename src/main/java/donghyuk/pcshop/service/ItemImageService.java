package donghyuk.pcshop.service;

import donghyuk.pcshop.entity.ItemImage;
import donghyuk.pcshop.repository.ItemImageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.thymeleaf.util.StringUtils;

import javax.persistence.EntityNotFoundException;
import java.io.IOException;

// 상품 이미지 저장/수정 서비스
@Service                    // 스프링 서비스 등록
@RequiredArgsConstructor    // final 멤버변수 생성자 의존성 주입.
@Transactional              // DB 트랜잭션 보장 (변경 처리를 위한 스냅샷 저장)
public class ItemImageService {
    @Value("${itemImageLocation}")          // application.properties 파일에서 itemImageLocation 정보를 할당.
    private String itemImageLocation;

    private final FileService fileService;
    private final ItemImageRepository itemImageRepository;

    // 상품 이미지 저장
    public void saveItemImage(ItemImage itemImage, MultipartFile itemImageFile) throws IOException {
        String oriImageName = itemImageFile.getOriginalFilename();
        String imageName = "";
        String imageUrl = "";

        // 파일 업로드
        if (!StringUtils.isEmpty(oriImageName)) {
            // 파일명이 있을 경우 파일을 서버에 업로드.
            imageName = fileService.uploadFile(itemImageLocation, oriImageName, itemImageFile.getBytes());
            // URL은 /images/item/ + 이미지이름.
            imageUrl = "/images/item/" + imageName;
        }

        // 상품 이미지 정보 저장
        itemImage.updateItemImage(oriImageName, imageName, imageUrl);
        itemImageRepository.save(itemImage);        // DB에 저장함.
    }

    // 상품 이미지 수정
    public void updateItemImage(Long itemImageId, MultipartFile itemImageFile) throws IOException {
        // 수정된 상품의 이미지가 있을 경우
        if (!itemImageFile.isEmpty()) {
            // 상품의 이미지 정보를 조회한다.
            ItemImage savedItemImage = itemImageRepository.findById(itemImageId).orElseThrow(EntityNotFoundException::new);
            // 이미 저장된 이미지 파일이 있을 경우에는 삭제한다.
            if (!StringUtils.isEmpty(savedItemImage.getImageName())) {
                fileService.deleteFile(itemImageLocation + "/" + savedItemImage.getImageName());
            }
            // 상품의 이미지를 서버에 업로드하고 DB에 저장한다.
            String oriImageName = itemImageFile.getOriginalFilename();
            String imageName = fileService.uploadFile(itemImageLocation, oriImageName, itemImageFile.getBytes());
            String imageUrl = "/images/item/" + imageName;
            savedItemImage.updateItemImage(oriImageName, imageName, imageUrl);
            //itemImageRepository.save(savedItemImage);
        }
    }
}
