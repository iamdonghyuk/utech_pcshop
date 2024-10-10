package donghyuk.pcshop.service;

import lombok.extern.java.Log;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.UUID;

// 파일을 업로드/삭제하기 위한 서비스
@Service            // 스프링 서비스 등록
@Log                // 로거 자동 생성
public class FileService {
    // 파일 업로드 함수
    public String uploadFile(String uploadPath, String originalFileName, byte[] fileData) throws IOException {
        UUID uuid = UUID.randomUUID();      // UUID를 이용한 임의의 파일명을 사용하기 위해 생성.
        String extension = originalFileName.substring(originalFileName.lastIndexOf(".")); // 파일확장자 분리
        // 임의 파일명과 확장자 결합
        String savedFileName = uuid.toString() + extension;     // 파일명
        // 실제 파일이 저장될 경로와 파일명
        String fileUploadFullUrl = uploadPath + "/" + savedFileName;    // 경로 + 파일명
        // 파일 쓰기
        FileOutputStream fos = new FileOutputStream(fileUploadFullUrl);
        fos.write(fileData);
        fos.close();
        // 저장된 파일명 반환
        return savedFileName;
    }

    // 파일 삭제 함수
    public void deleteFile(String filePath) {
        // 전달받은 파일경로+파일명으로 파일 객체 생성
        File deleteFile = new File(filePath);
        // 파일의 존재 확인, 있으면 삭제, 없으면 에러.
        if (deleteFile.exists()) {
            deleteFile.delete();
            log.info("파일을 삭제하였습니다.");
        } else {
            log.info("파일이 존재하지 않습니다.");
        }
    }
}
