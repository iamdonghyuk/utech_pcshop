package donghyuk.pcshop.repository;

import donghyuk.pcshop.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

// 회원 리포지토리 인터페이스
public interface MemberRepository extends JpaRepository<Member, Long> {
    // 회원 정보를 이메일 주소로 찾는 메소드
    Member findByEmail(String email);
}
