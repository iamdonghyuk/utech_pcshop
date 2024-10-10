package donghyuk.pcshop.entity;

import donghyuk.pcshop.constant.Role;
import donghyuk.pcshop.dto.SignUpFormDto;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.persistence.*;

// 회원 정보를 관리하는 클래스
@Entity
@Table(name = "member")
@Getter @Setter @ToString
public class Member extends BaseEntity {
    @Id
    @Column(name = "member_id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(unique = true)
    private String email;           // 이메일 주소, 유니크
    private String name;            // 이름
    private String address;         // 주소
    private String password;        // 비밀번호

    @Enumerated(EnumType.STRING)
    private Role role;              // 회원 타입 (user, admin)

    // 일반 사용자를 생성하는 함수
    public static Member createUser(SignUpFormDto signUpFormDto, PasswordEncoder passwordEncoder) {
        return Member.createMember(signUpFormDto, passwordEncoder, Role.USER);
    }
    // 관리자를 생성하는 함수
    public static Member createAdmin(SignUpFormDto signUpFormDto, PasswordEncoder passwordEncoder) {
        return Member.createMember(signUpFormDto, passwordEncoder, Role.ADMIN);
    }

    // 신규 사용자를 생성하는 함수
    private static Member createMember(SignUpFormDto signUpFormDto, PasswordEncoder passwordEncoder, Role role) {
        Member member = new Member();
        member.setName(signUpFormDto.getName());
        member.setEmail(signUpFormDto.getEmail());
        member.setAddress(signUpFormDto.getAddress());
        // 비밀번호는 암호화하여 저장한다.
        String password = passwordEncoder.encode(signUpFormDto.getPassword());
        member.setPassword(password);
        member.setRole(role);
        return member;
    }
}

