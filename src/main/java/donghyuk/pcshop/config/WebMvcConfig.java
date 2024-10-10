package donghyuk.pcshop.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

// 스프링에 추가적인 설정을 위해서 사용하는 인터페이스
// 이미지 업로드 경로 (실제 파일의 위치)와 웹 URL의 경로를 매핑하기 위해서 사용함.
@Configuration          // 스프링 구성 클래스 등록
public class WebMvcConfig implements WebMvcConfigurer {
    // application.properties의 uploadPath를 읽어 uploadPath 변수에 할당.
    @Value("${uploadPath}")
    String uploadPath;

    // /images/와 uploadPath를 서로 매핑시켜 연결해준다.
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/images/**")
                .addResourceLocations(uploadPath);
    }
}
