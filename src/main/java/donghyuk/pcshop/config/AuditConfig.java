package donghyuk.pcshop.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

// 사용자 정보를 자동으로 입력받도록 하기 위해 설정 클래스
// BaseEntity 클래스의 createdBy / modifiedBy 가 자동으로 들어가진다.
@Configuration          // 구성 클래스로 등록
@EnableJpaAuditing      // JPA의 Auditing 기능 활성화
public class AuditConfig {
    // 데이터 생성자와 수정자 정보를 자동으로 저장하는 함수
    @Bean               // 구성 클래스 메소드
    public AuditorAware<String> auditorProvider() {
        return new AuditorAwareImpl();
    }
}
