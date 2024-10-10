package donghyuk.pcshop.controller;

import donghyuk.pcshop.dto.ItemFormDto;
import donghyuk.pcshop.dto.ItemSearchDto;
import donghyuk.pcshop.entity.Item;
import donghyuk.pcshop.service.ItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import javax.persistence.EntityNotFoundException;
import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@Controller                 // 스프링 컨트롤러 등록
@RequiredArgsConstructor    // 생성자에 ItemService 주입
public class ItemController {
    private final ItemService itemService;

    // 관리자 상품 등록 페이지 (GET)
    @GetMapping(value = "/admin/item/new")
    public String itemForm(ItemFormDto itemFormDto, Model model) {
        // ItemFormDto를 HTML 렌더링할 모델에 추가.
        model.addAttribute("itemFormDto", itemFormDto);
        // HTML = item/itemForm.html
        return "item/itemForm";
    }

    // 관리자 상품 등록 페이지 (POST) - 데이터 저장.
    @PostMapping(value = "/admin/item/new")
    public String itemNew(@Valid ItemFormDto itemFormDto, BindingResult bindingResult, Model model,
                          @RequestParam(name = "itemImageFile") List<MultipartFile> itemImageFileList) {
        // 타임리프에서 가져온 데이러를 자바 객체로 연결, 에러 발생하면 item/itemForm.html로 이동.
        if (bindingResult.hasErrors()) {
            return "item/itemForm";
        }
        // 상품이미지 리스트가 비어 있거나 상품 ID가 없을 경우에는 에러.
        if (itemImageFileList.get(0).isEmpty() && itemFormDto.getId() == null) {
            model.addAttribute("errorMessage",
                    "첫번째 상품 이미지는 필수 입력 값 입니다.");
            return "item/itemForm";
        }
        try {
            // 입력받은 상품 정보와 이미지 리스트를 저장.
            itemService.saveItem(itemFormDto, itemImageFileList);
        } catch (Exception e) {
            model.addAttribute("errorMessage", "상품 등록 중 에러가 발생하였습니다!");
            return "item/itemForm";
        }
        // 성공하면 홈으로 이동.
        return "redirect:/";
    }

    // 관리자 상품 관리 페이지
    // 상품 리스트가 많아 페이지 발생시는 페이지 정보도 같이 처리 (페이지 정보는 선택사항)
    @GetMapping(value = {"/admin/items", "/admin/items/{page}"})
    public String itemManage(ItemSearchDto itemSearchDto,
                             @PathVariable(name = "page") Optional<Integer> page, Model model) {
        // PageRequest.of() 메소드를 통해 Pageable 객체 생성
        // 페이지 정보가 없을 경우 시작 페이지는 0, 있으면 현재 페이지, 페이지 사이즈는 3
        Pageable pageable = PageRequest.of(page.isPresent() ? page.get() : 0, 3);
        Page<Item> items = itemService.getAdminItemPage(itemSearchDto, pageable);
        model.addAttribute("items", items);
        model.addAttribute("itemSearchDto", itemSearchDto);
        model.addAttribute("maxPage", 5);
        // HTML = item/itemManage.html
        return "item/itemManage";
    }

    // 관리자 상품 수정 페이지 (GET-조회 - 화면에 표시)
    @GetMapping(value = "/admin/item/{itemId}") // 수정할 상품ID를 {itemId}로 받는다.
    public String itemDetail(@PathVariable(name = "itemId") Long itemId, Model model) {
        try {
            // itemId로 수정할 상품 정보를 조회하여 ItemFormDto로 할당한다.
            ItemFormDto itemFormDto = itemService.getItemDetail(itemId);
            model.addAttribute("itemFormDto", itemFormDto);
        } catch (EntityNotFoundException e) {
            // DB에 없다는 에러가 발생하면 에러 메시지를 반환.
            model.addAttribute("errorMessage", "등록되지 않은 상품입니다.");
            model.addAttribute("itemFormDto", new ItemFormDto());
        }
        // HTML = item/itemForm.hthml
        return "item/itemForm";
    }

    // 관리자 상품 수정 페이지 (POST-저장)
    @PostMapping(value = "/admin/item/{itemId}") // 수정할 상품ID를 {itemId}로 받는다.
    // valid 검증하는 어노테이션
    public String itemUpdate(@Valid ItemFormDto itemFormDto, BindingResult bindingResult, Model model,
                             @RequestParam(name = "itemImageFile") List<MultipartFile> itemImageFileList) {
        // 타임리프에서 가져온 데이러를 자바 객체로 연결, 에러 발생하면 item/itemForm.html로 이동.
        if (bindingResult.hasErrors()) {
            return "item/itemForm";
        }
        // 상품이미지 리스트가 비어 있거나 상품 ID가 없을 경우에는 에러.
        if (itemImageFileList.get(0).isEmpty() && itemFormDto.getId() == null) {
            model.addAttribute("errorMessage",
                    "첫번째 상품 이미지는 필수 입력 값 입니다.");
            return "item/itemForm";
        }
        try {
            // 입력받은 상품 정보와 이미지 리스트를 저장.
            itemService.updateItem(itemFormDto, itemImageFileList);
        } catch (Exception e) {
            model.addAttribute("errorMessage", "상품 정보 수정 중 에러가 발생하였습니다!");
            return "item/itemForm";
        }
        // 성공시 홈으로 이동.
        return "redirect:/";
    }

    // 일반 사용자의 상품 상세 조회 페이지 (GET-조회)
    @GetMapping(value = "/item/{itemId}") // 조회할 상품ID를 {itemId}로 받는다.
    public String itemDetail(Model model, @PathVariable("itemId") Long itemId) {
        // itemId로 받은 상품 정보를 조회하여 ItemFormDto 클래스에 넣어 itemDetail.html에 렌더링에 사용한다.
        ItemFormDto itemFormDto = itemService.getItemDetail(itemId);
        model.addAttribute("item", itemFormDto);
        // HTML = item/itemDetail.html
        return "item/itemDetail";
    }
}
