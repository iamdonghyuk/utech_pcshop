package donghyuk.pcshop.config;

import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

// AuditAware의 구현체
public class AuditorAwareImpl implements AuditorAware<String> {
    @Override
    public Optional<String> getCurrentAuditor() {
        // 현재 로그인한 사용자의 정보를 가져온다.
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userId = "";
        // 사용자가 인증된 경우 사용자 정보를 userId에 할당한다.
        if (authentication != null) {
            userId = authentication.getName();
        }
        // NPE (Null Point Exception 에러가 발생하지 않도록 Optional 클래스 사용.
        return Optional.of(userId);
    }
}
