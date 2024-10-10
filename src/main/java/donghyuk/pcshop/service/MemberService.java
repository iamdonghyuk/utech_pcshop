package donghyuk.pcshop.service;

import donghyuk.pcshop.entity.Member;
import donghyuk.pcshop.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

// 회원 관련 내부 로직 처리
@Service                    // 서비스 클래스로 등록
@RequiredArgsConstructor    // MemberRepository를 생성자를 통해 의존성 주입
public class MemberService implements UserDetailsService {
    private final MemberRepository memberRepository;

    // 전달받은 회원 정보를 저장.
    public Member saveMember(Member member) {
        // 중복 검사.
        validateDuplicateMember(member);
        return memberRepository.save(member);
    }

    // 전달받은 회원 정보가 존재하는지 확인 (이메일로 중복 여부 판단)
    public void validateDuplicateMember(Member member) {
        // 회원정보중 이메일로 가입된 회원이 있는 조회.
        Member findMember = memberRepository.findByEmail(member.getEmail());
        if (findMember != null) {
            // 조회되면 이미 가입된 회원, 에러 발생시킨다.
            throw new IllegalStateException("이미 가입된 회원입니다.");
        }
    }

    // 로그인시에 사용자명(email)을 회원정보를 조회 / 스프링 시큐리티의 UserDetails 로 반환한다.
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Member member = memberRepository.findByEmail(email);
        if (member == null) {
            // 조회되지 않으면 회원 없음 에러 발생 시킨다.
            throw new UsernameNotFoundException(email);
        }
        return User.builder()
                .username(member.getEmail())
                .password(member.getPassword())
                .roles(member.getRole().toString())
                .build();
    }
}
