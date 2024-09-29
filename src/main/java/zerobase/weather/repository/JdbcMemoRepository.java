package zerobase.weather.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import zerobase.weather.domain.Memo;

import javax.sql.DataSource;
import java.util.List;
import java.util.Optional;

@Repository
public class JdbcMemoRepository {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public JdbcMemoRepository(DataSource dataSource) { //DataSource: application.properties에서 기입해준 db관련 정보들임 
        //@Autowired: 자동으로 스캔해서 가져옴

        jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public Memo save(Memo memo) {
        String sql = "insert into memo values(?,?)";
        jdbcTemplate.update(sql, memo.getId(), memo.getText());
        return memo;
    }

    public List<Memo> findAll() {
        String sql="select * from memo";
       return jdbcTemplate.query(sql, memoRowMapper());
    }

    public Optional<Memo> findById(int id) {
        String sql = "select * from memo where id = ?";
        //optinal -> 혹시 찾은 개체가 null 이 있을경우 null 처리를 용이하게 처리해줌..혹시모를 널값을 처리하도록 자바에서 지원
        return jdbcTemplate.query(sql, memoRowMapper(), id).stream().findFirst();
    }


    private RowMapper<Memo> memoRowMapper() {
        //가져오는데이터가 리져트셋으로 가져옴 {id=1, text='this is memo'}
        //가져온형식을 클래스에 대입시켜야함. 클래스에 맞는 형식으로 멥핑해주기

        return (rs, rowNum) -> new Memo(
                rs.getInt("id"),
                rs.getString("text")
        );

    }
}
