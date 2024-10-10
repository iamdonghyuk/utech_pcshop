package donghyuk.pcshop;

import lombok.extern.java.Log;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@Log
@SpringBootApplication
public class PcshopApplication {
	public static void main(String[] args) {
		SpringApplication.run(PcshopApplication.class, args);
		log.info("PC 쇼핑몰 서버가 시작되었습니다.");
	}
}
