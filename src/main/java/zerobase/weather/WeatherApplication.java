package zerobase.weather;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableTransactionManagement
@EnableScheduling
public class WeatherApplication { //transaction과 스케쥴 기능 사용할수있게 @EnableTransactionManagement @EnableScheduling 달아줌

    public static void main(String[] args) {
        SpringApplication.run(WeatherApplication.class, args);
    }

}
