package zerobase.weather.controller;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import zerobase.weather.domain.Diary;
import zerobase.weather.service.DiaryService;

import java.time.LocalDate;
import java.util.List;

//Exception Handler -> @Controller, @RestController의 예외를 하나의 메서드에서 처리해주는 기능 . 부분적 예외잡기아닌 전역적인 예외잡기 , 코드 가독성도 높히고
//모든 컨트롤러 아니고 하나의 컨트롤러 안에서만.. 적용 -> 이것의 보완 ControllerAdivece (모든 대상을 하여 예외 발생 잡아줌)

@RestController
public class DiaryController { //@RestController: 기본 @Controller기능 + 상태코드를 컨트롤러에서 지정해서 내려줌...

    private final DiaryService diaryService;

    public DiaryController(DiaryService diaryService) {
        this.diaryService = diaryService;
    }


    //어떤 패스로 요청할때 이 함수가 동작할지 명시
    @ApiOperation(value = "일기 텍스트와 날씨를 이용해서 DB에 일기 저장", notes = "이것은 노트")
    @PostMapping("/create/diary")
    void createDiary(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date, @RequestBody String text) { //RequestParam: 클라이언트 요청시 넣어주는 파라미터 . @DateTimeFormat 요청받는 데이트 파라미터를 형식을 지정
        //RequestBody : post 요청이니까 바디쪽 내용 받아줌
        //일반적으로 브라우져에서 요청 확인하는 법은 get으로 인식함.. 그래서 post 요청을 확인할떄 브라우져를 이용하는것은 적합하지 않다. 브라우져는 기본 캐싱을함. 캐싱 빠르게 처리하느라.. 정확하게 정보전달 어려움.
        diaryService.createDiary(date, text);
    }

    @ApiOperation("선택한 날짜의 모든 일기 데이터를 가져옵니다")
    @GetMapping("/read/diary")
    List<Diary> readDiary(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return diaryService.readDiary(date);
    }

    @ApiOperation("선택한 기간중의 모든 일기 데이터를 가져옵니다")
    @GetMapping("/read/diaries")
    List<Diary> readDiaries(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) @ApiParam(value = "날짜 형식 : 조회할 기간의 첫째날", example = "2020-01-02") LocalDate startDate,
                            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) @ApiParam(value = "날짜 형식 : 조회할 기간의 마지막날", example = "2020-02-02") LocalDate endDate) {
        return diaryService.readDiaries(startDate, endDate);
    }

    @PutMapping("/update/diary")
    void updateDiary(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date, @RequestBody String text) {
        diaryService.updateDiary(date, text);
    }

    @DeleteMapping("/delete/diary")
    void deleteDiary(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        diaryService.deleteDiary(date);
    }

}
