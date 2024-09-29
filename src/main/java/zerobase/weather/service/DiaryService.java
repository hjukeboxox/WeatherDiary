package zerobase.weather.service;


import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import zerobase.weather.WeatherApplication;
import zerobase.weather.domain.DateWeather;
import zerobase.weather.domain.Diary;
import zerobase.weather.error.InvalidDate;
import zerobase.weather.repository.DateWeatherRepository;
import zerobase.weather.repository.DiaryRepository;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional(readOnly = true)
public class DiaryService {

    @Value("${openweathermap.key}")
    //@Value: 스프링에 지정된 변수 openweathermap.key 를 apiKey 객체에 넣어줌
    //e.g) 여러환경에서 벨류값이 다른경우는 실제 db에 반영되면 안되서..  테스트 를 위해.. 환경마다 다르게 지정해서 쓰려고
    private String apiKey;

    private final DiaryRepository diaryRepository;
    private final DateWeatherRepository dateWeatherRepository;

    //어떤 패스에서 로거를 가져올것이냐? -> 프로젝트 전체에서 한개만 만들어서 쓰려고함
    private static final Logger logger = LoggerFactory.getLogger(WeatherApplication.class);

    public DiaryService(DiaryRepository diaryRepository, DateWeatherRepository dateWeatherRepository) {
        this.diaryRepository = diaryRepository;
        //다이어리서비스 빈이 생성될때 다이어리 리포지토리를 가져오겠다.
        this.dateWeatherRepository = dateWeatherRepository;
    }

    @Transactional
    @Scheduled(cron = "0 0 1 * * *")
    public void saveWeatherDate() { // 초분시일월
        logger.info("오늘도 날씨 데이터 잘 가져옴");
        dateWeatherRepository.save(getWeatherFromApi());
    }


    @Transactional(isolation = Isolation.SERIALIZABLE)
    public void createDiary(LocalDate date, String text) {
//        open weather map에서 날씨 데이터 가져오기
//        String weatherData = getWeatherString();
//
//        받아온 날씨 json 파싱하기
//        Map<String, Object> parseWeather = parseWeather(weatherData);
//        파싱된 데이터 + 일기 값 우리 db에 넣기

        logger.info("started to create diary");

        //날씨 데이터 가져오기(API에서 가져오기 or DB에서 가져오기)
        DateWeather dateWeather = getDateWeather(date);


        //domain에 Diary  에서 noArgumentContracture 썼기때문에 new Diary(); 쓴느게 가능 .. 생성자 생성 가능.
        Diary nowDiary = new Diary();

        nowDiary.setDateWeather(dateWeather);
        nowDiary.setText(text);
        nowDiary.setDate(date);
        diaryRepository.save(nowDiary);

        logger.info("end to create diary");

    }

    private DateWeather getDateWeather(LocalDate date) {
        List<DateWeather> dateWeatherListFromDB = dateWeatherRepository.findAllByDate(date);
        if (dateWeatherListFromDB.size() == 0) {
            //새로 api에서 날씨 정보를 가져와야 한다
            //정책: 현재날씨를 가져오도록 > 혹은 날씨없이 일기를 쓰도록
            return getWeatherFromApi();
        } else {
            return dateWeatherListFromDB.get(0);
        }
    }

    private DateWeather getWeatherFromApi() {
        String weatherData = getWeatherString();
        Map<String, Object> parseWeather = parseWeather(weatherData);
        DateWeather dateWeather = new DateWeather();
        dateWeather.setDate(LocalDate.now());
        dateWeather.setWeather(parseWeather.get("main").toString());
        dateWeather.setIcon(parseWeather.get("icon").toString());
        dateWeather.setTemperature((Double) parseWeather.get("temp"));
        return dateWeather;
    }

    @Transactional(readOnly = true)
    public List<Diary> readDiary(LocalDate date) {

//        if (date.isAfter(LocalDate.ofYearDay(3050,1))){
//            throw new InvalidDate();
//        }

        logger.debug("read diary");
        return diaryRepository.findAllByDate(date);
    }


    public List<Diary> readDiaries(LocalDate startDate, LocalDate endDate) {
        return diaryRepository.findAllByDateBetween(startDate, endDate);
    }

    @Transactional
    public void updateDiary(LocalDate date, String text) {
        Diary nowDiary = diaryRepository.getFirstByDate(date);
        nowDiary.setText(text);
        //위에 바꿔준 내용 db반영... save는 id 는 그대로인체로 다른게 바뀐 후 save하면 수정으로 인식함.. id도 추가해서 들어가면 새로운 로우 추가함.
        diaryRepository.save(nowDiary);
    }

    @Transactional
    public void deleteDiary(LocalDate date) {
        diaryRepository.deleteAllByDate(date);
    }

    private String getWeatherString() {
        String apiUrl = "https://api.openweathermap.org/data/2.5/weather?q=seoul&appid=" + apiKey;

        try {
            //string -> URL 형식 바꿔주기
            URL url = new URL(apiUrl);
            //apiURL 을 HTTP형식으로 연결함 . 요청을 보낼수있는 HttpURLConnection을 연다
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            //get요청을 보냄
            connection.setRequestMethod("GET");
            //응답결과
            int responseCode = connection.getResponseCode();
            BufferedReader br;
            if (responseCode == 200) {
                //응답결과 br에 넣음 br를 쓰면 속도 및 성능향상..
                br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            } else {
                br = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
            }
            String inputLine;
            //stringBuilder에다가 결과 쌓기
            StringBuilder response = new StringBuilder();
            while ((inputLine = br.readLine()) != null) {
                response.append(inputLine);
            }
            br.close();


            return response.toString();
        } catch (Exception e) {
            return "failed to get response";
        }


    }

    private Map<String, Object> parseWeather(String jsonString) {
        JSONParser jsonParser = new JSONParser();
        JSONObject jsonObject;

        //중괄호가 열렸는데 닫혀있지 않을떄 할떄 유효하지않는 문자열 존재할수도있어서.. 파싱이 제대로 안될수있어서 파싱시 에러 잡아주기
        try {
            jsonObject = (JSONObject) jsonParser.parse(jsonString);
        } catch (ParseException e) {
            throw new RuntimeException(e);
            // 예외를 처리해서 없애는게 아니라 그냥 예외가 닸다고 .. 알려줌
        }
        Map<String, Object> resultMap = new HashMap<>();

        JSONObject mainData = (JSONObject) jsonObject.get("main");
        resultMap.put("temp", mainData.get("temp"));
        JSONArray weatherArray = (JSONArray) jsonObject.get("weather");
        JSONObject weatherData = (JSONObject) weatherArray.get(0);
        resultMap.put("main", weatherData.get("main"));
        resultMap.put("icon", weatherData.get("icon"));
        return resultMap;

    }
}
