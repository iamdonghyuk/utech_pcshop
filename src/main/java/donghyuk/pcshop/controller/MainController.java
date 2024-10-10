package donghyuk.pcshop.controller;

import donghyuk.pcshop.dto.ItemSearchDto;
import donghyuk.pcshop.dto.MainItemDto;
import donghyuk.pcshop.service.ItemService;
import lombok.RequiredArgsConstructor;
import org.jboss.jandex.Main;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import java.util.Optional;

@Controller         // 스프링 컨트롤러 등록
@RequiredArgsConstructor    // Item Service를 생성자를 통해 의존성 주입.
public class MainController {
    private final ItemService itemService;

    // 메인 홈페이지 GET 매핑.
    @GetMapping(value = "/")
    public String index(ItemSearchDto itemSearchDto, Optional<Integer> page, Model model) {
        // 페이지 객체 생성, 현재 페이지 정보 또는 없으면 0, 기본 페이지 사이즈는 5
        Pageable pageable = PageRequest.of(page.isPresent() ? page.get() : 0, 2);
        Page<MainItemDto> items = itemService.getMainItemPage(itemSearchDto, pageable);
        // HTML 렌더링에 사용할 모델들 추가 (상품들, 검색조건, 최대 페이지)
        model.addAttribute("items", items);
        model.addAttribute("itemSearchDto", itemSearchDto);
        model.addAttribute("maxPage", 10);
        // HTML = index.html
        return "index";
    }
}
