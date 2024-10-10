package donghyuk.pcshop.repository;

import com.querydsl.core.QueryResults;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import donghyuk.pcshop.constant.ItemStatus;
import donghyuk.pcshop.dto.ItemSearchDto;
import donghyuk.pcshop.dto.MainItemDto;
import donghyuk.pcshop.dto.QMainItemDto;
import donghyuk.pcshop.entity.Item;
import donghyuk.pcshop.entity.QItem;
import donghyuk.pcshop.entity.QItemImage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.thymeleaf.util.StringUtils;
import javax.persistence.EntityManager;
import java.time.LocalDateTime;
import java.util.List;

// ItemRepositoryCustom의 구현체
public class ItemRepositoryCustomImpl implements ItemRepositoryCustom {
    private JPAQueryFactory queryFactory;

    // 생성자 의존성 주입을 통해 JPAQueryFactory(EntityManager) 주입
    public ItemRepositoryCustomImpl(EntityManager em){
        this.queryFactory = new JPAQueryFactory(em);
    }

    // 상품 등록일로 조회.
    private BooleanExpression createdDatesAfter(String searchDateType) {
        LocalDateTime dateTime = LocalDateTime.now();
        // 날짜 검색 조건이 들어 있으면 오늘로부터 해당 조건을 빼서 검색 기준일을 계산한다.
        if (StringUtils.equals("all", searchDateType) || searchDateType == null) {
            return null;
        } else if (StringUtils.equals("1d",searchDateType)) {
            dateTime = dateTime.minusDays(1);
        } else if (StringUtils.equals("1w",searchDateType)) {
            dateTime = dateTime.minusWeeks(1);
        } else if (StringUtils.equals("1m",searchDateType)) {
            dateTime = dateTime.minusMonths(1);
        } else if (StringUtils.equals("6m",searchDateType)) {
            dateTime = dateTime.minusMonths(6);
        }
        // 입력받은 날짜 조건으로 조건 추가.
        return QItem.item.createdTime.after(dateTime);
    }

    // 상품 재고 상태로 조회
    private BooleanExpression searchItemStatusEq(ItemStatus searchStatus) {
        // 입력받은 상품 상태로 조건 추가.
        return searchStatus == null ? null : QItem.item.itemStatus.eq(searchStatus);
    }

    // 상품을 등록한 ID 또는 상품명으로 조회.
    private BooleanExpression searchByLike(String searchBy, String searchQuery) {
        if (StringUtils.equals("itemName", searchBy)) {
            // 상품명으로 조건 추가 (Like 검색)
            return QItem.item.itemName.like("%" + searchQuery + "%");
        } else if (StringUtils.equals("createdBy", searchBy)) {
            // 상품 등록ID로 조건 추가 (Like 검색)
            return QItem.item.createdBy.like("%" + searchQuery + "%");
        }
        return null;
    }

    // 쿼리만 입력받았을 때, 상품명을 like로 조회.
    private BooleanExpression itemNameLike(String searchQuery) {
        return StringUtils.isEmpty(searchQuery) ? null : QItem.item.itemName.like("%" + searchQuery + "%");
    }

    // 관리자가 상품 조회, 결과는 페이지 객체로 반환
    @Override
    public Page<Item> getAdminItemPage(ItemSearchDto itemSearchDto, Pageable pageable) {
        // queryFactory 를 이용하여 쿼리문 생성
        QueryResults<Item> results = queryFactory
                .selectFrom(QItem.item)
                .where(createdDatesAfter(itemSearchDto.getSearchDateType()),
                       searchItemStatusEq(itemSearchDto.getSearchItemStatus()),
                       searchByLike(itemSearchDto.getSearchBy(), itemSearchDto.getSearchQuery()))
                .orderBy(QItem.item.id.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetchResults();
        // 조회 결과를 상품 리스트로 담는다.
        List<Item> content = results.getResults();
        // 조회 결과 갯수 확인.
        long total = results.getTotal();
        // 조회결과와 레코드수를 페이지 구현체 형태로 반환.
        return new PageImpl<>(content, pageable, total);
    }

    // 일반 회원이 상품을 조회, 결과는 MainItemDto로 반환.
    @Override
    public Page<MainItemDto> getMainItemPage(ItemSearchDto itemSearchDto, Pageable pageable) {
        QItem item = QItem.item;
        QItemImage itemImage = QItemImage.itemImage;
        QueryResults<MainItemDto> result = queryFactory
                .select(
                        new QMainItemDto(
                                item.id,
                                item.itemName,
                                item.itemDetail,
                                itemImage.imageUrl,
                                item.price)
                )
                .from(itemImage)
                .join(itemImage.item, item)
                .where(itemImage.isMainImage.eq("Y"))
                .where(itemNameLike(itemSearchDto.getSearchQuery()))
                .orderBy(item.id.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetchResults();
        // 조회 결과를 상품 리스트로 담는다.
        List<MainItemDto> content = result.getResults();
        // 조회 결과 갯수 확인.
        long total = result.getTotal();
        // 조회결과와 레코드수를 페이지 구현체 형태로 반환.
        return new PageImpl<>(content, pageable, total);
    }
}
