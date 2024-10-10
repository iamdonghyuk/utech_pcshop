package donghyuk.pcshop.dto;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

// 사용자 등록/수정 Dto 클래스
@Getter @Setter     // getter & setter 자동 생성
public class SignUpFormDto {
    @NotBlank(message = "이름은 필수 입력 값입니다.")
    private String name;

    @NotBlank(message = "이메일은 필수 입력 값입니다.")
    @Email(message = "이메일 형식으로 입력해주세요.")
    private String email;

    @NotBlank(message = "비밀번호는 필수 입력 값입니다.")
    @Length(min = 8, max = 16, message = "비밀번호는8자 이상, 16자 이하로 입력해주세요")
    private String password;

    @NotBlank(message = "주소는 필수 입력 값입니다.")
    private String address;
}
