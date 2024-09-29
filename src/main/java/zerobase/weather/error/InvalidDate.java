package zerobase.weather.error;

public class InvalidDate extends RuntimeException { //RuntimeException 에러상황이 났을때 메세지보여주도록 설정가능
    private static final String MESSAGE= "너무 과거 혹은 미래의 날짜입니다.";

    public InvalidDate() {
        super(MESSAGE); //불리는 순간 메세지도 같이 반환됨
    }
}
