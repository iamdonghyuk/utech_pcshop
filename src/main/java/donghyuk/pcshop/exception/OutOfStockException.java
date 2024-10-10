package donghyuk.pcshop.exception;

// 품절 예외 처리 클래스
public class OutOfStockException extends RuntimeException {
    public OutOfStockException(String message) {
        super(message);
    }
}
