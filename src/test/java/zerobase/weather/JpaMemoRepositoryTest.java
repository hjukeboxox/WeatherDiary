package zerobase.weather;


import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import zerobase.weather.domain.Memo;
import zerobase.weather.repository.JpaMemoRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
@Transactional
class JpaMemoRepositoryTest { //테스트 동작시키고 디비에 변경사항 롤백처리하기

    @Autowired
    JpaMemoRepository jpaMemoRepository;

    @Test
    void findAllMemoTest() {
        List<Memo> memoList = jpaMemoRepository.findAll();
        System.out.println(memoList);
        assertNotNull(memoList);
    }


    @Test
    void insertMemoTest() {
        //given
        Memo newMemo = new Memo(10,"this is jpa memo");
        //when
        jpaMemoRepository.save(newMemo);
        //then
        List<Memo> memoList = jpaMemoRepository.findAll();
        assertTrue(memoList.size()>0);
    }

    @Test
    void findByIdTest() {
        //given
        Memo newMemo = new Memo(11,"jpa");
        //11은 의미가 없음. 여기서 11이라고 해도.. 디비에선 자동으로 만들어줘서 다른값일수있음
        //when
        Memo memo = jpaMemoRepository.save(newMemo);
        System.out.println(memo.getId());
        //then
        Optional<Memo> result = jpaMemoRepository.findById(11);
        assertEquals(result.get().getText(),"jpa");
    }

}