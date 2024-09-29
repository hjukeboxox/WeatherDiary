package zerobase.weather.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import zerobase.weather.domain.Diary;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface DiaryRepository extends JpaRepository<Diary, Integer> {
    List<Diary> findAllByDate(LocalDate date);

    List<Diary> findAllByDateBetween(LocalDate startDate, LocalDate endDate);


    //select * from 테이블 where date= date limit 1; 느낌임...

    Diary getFirstByDate(LocalDate date);


    void deleteAllByDate(LocalDate date);
    //@Transactional 추가 안하면 안지워짐..
    //호출시킴으로서 데이터상태가 변경되는걸 원하지않으면 과정에서 변경된거를 원복시켜놓는게 @Transactional
    //또다른  @Transactional 기능으로선 스프링과 db가 데이터 주고 받을때 예외들이나 오류들이 발생함

    // 작업단위는 나누기 나름... 트랜잭션: 오늘일기 작성하기(테이터 상태를 변화시키키위한 작업단위) > (1) 오늘 날씨데이터가져와서 (2) 일기를 DB에 저장하기
    //(1) 에서 문제가 생기면 (2)로 못가고 (1)을 롤백을시킴.

    //트랜잭션 기능이 적용된 프록시 객체 생성 수행시간이 증가.. 줄어서 처리하니까..

    /*세부설정들
    1. 격리수준(Isolation) - 5가지 ..말그대로 격리수준
    2. 전파수준(Prepagation) -4개 이상 ...트랜잭션도중 다른 트랜잭션을 호출하는 상황에 결정하는 속성값
    3  readOnly속성 - 
    4. 트랜잭션 롤백 예외
    5. timeout 속성*/

}
