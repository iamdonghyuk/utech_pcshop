package donghyuk.pcshop.controller;

import donghyuk.pcshop.dto.SignUpFormDto;
import donghyuk.pcshop.entity.Member;
import donghyuk.pcshop.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;

@Controller                 // 스프링 컨트롤러 등록
@RequiredArgsConstructor    // PasswordEncoder, MemberService를 생성자를 통해 의존성 주입
@RequestMapping("/member")  // /member 경로 설정.
public class MemberController {
    private final PasswordEncoder passwordEncoder;
    private final MemberService memberService;

    // 신규 회원 가입 페이지 (GET)
    @GetMapping("/new")
    public String memberSignUpForm(SignUpFormDto signUpFormDto, Model model) {
        // SignUpFormDto를 HTML 렌더링에 사용.
        model.addAttribute("signUpFormDto", signUpFormDto);
        // HTML = member/signUpForm.html
        return "member/signUpForm";
    }

    // 신규 회원 가입 페이지 (POST - 저장)
    @PostMapping("/new")
    public String memberSingUp(@Valid SignUpFormDto signUpFormDto, BindingResult bindingResult, Model model) {
        // 타임리프로 전달 받은 데이터를 SignUpFormDto에 매핑시 에러 발생하면 SignUpForm.html로 이동.
        if (bindingResult.hasErrors()) {
            return "member/signUpForm";
        }
        try {
            // 입력 받은 정보를 이용하여 회원 객체 생성, 암호는 스프링시큐리티의 PasswordEncoder 클래스를 통해 암호화.
            Member member = Member.createUser(signUpFormDto, passwordEncoder);
            // 객체를 DB에 저장한다.
            memberService.saveMember(member);
        } catch (Exception e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "member/signUpForm";
        }
        // 성공시 홈으로 이동.
        return "redirect:/";
    }

    // 신규 관리자 가입 페이지 (GET)
    @GetMapping("/newadmin")
    public String adminSignUpForm(SignUpFormDto signUpFormDto, Model model) {
        // SignUpFormDto를 HTML 렌더링에 사용.
        model.addAttribute("signUpFormDto", signUpFormDto);
        // HTML = member/signUpFormAdmin.html
        return "member/signUpFormAdmin";
    }

    // 신규 관리자 가입 페이지 (POST - 저장)
    @PostMapping("/newadmin")
    public String adminSingUp(@Valid SignUpFormDto signUpFormDto, BindingResult bindingResult, Model model) {
        // 타임리프로 전달 받은 데이터를 SignUpFormDto에 매핑시 에러 발생하면 SignUpForm.html로 이동.
        if (bindingResult.hasErrors()) {
            return "member/signUpFormAdmin";
        }
        try {
            // 입력 받은 정보를 이용하여 회원 객체 생성, 암호는 스프링시큐리티의 PasswordEncoder 클래스를 통해 암호화.
            Member member = Member.createAdmin(signUpFormDto, passwordEncoder);
            // 객체를 DB에 저장한다.
            memberService.saveMember(member);
        } catch (Exception e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "member/signUpFormAdmin";
        }
        // 성공시 홈으로 이동.
        return "redirect:/";
    }

    // 로그인 페이지
    @GetMapping("/login")       // GET
    public String memberLogin() {
        // HTML = member/loginForm.html
        return "member/loginForm";
    }

    // 로그인 실패 페이지
    @GetMapping("/login/fail")
    public String memberLoginFail(Model model) {
        // 로그인 실패 메시지를 화면에 표시.
        model.addAttribute("loginFailureMessage", "아이디 또는 비밀번호를 확인해주세요.");
        // HTML = member/loginForm.html
        return "member/loginForm";
    }
}
