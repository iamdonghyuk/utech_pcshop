package donghyuk.pcshop.entity;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import javax.persistence.Column;
import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;

// 데이터의 생성 및 수정자를 자동으로 저장하는 abstract entity class
// 생성 시간과 수정 시간을 자동으로 저장하는 BaseTimeEntity를 상속받는다.
@Getter @Setter
@MappedSuperclass
@EntityListeners(value = {AuditingEntityListener.class})
public abstract class BaseEntity extends BaseTimeEntity {
    @CreatedBy
    @Column(updatable = false)
    private String createdBy;           // 데이터 생성자

    @LastModifiedBy
    private String modifiedBy;          // 데이터 최종 수정자
}
