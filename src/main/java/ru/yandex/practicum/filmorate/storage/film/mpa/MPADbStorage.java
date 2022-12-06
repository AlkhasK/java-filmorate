package ru.yandex.practicum.filmorate.storage.film.mpa;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.controller.exception.EntityNotFoundException;
import ru.yandex.practicum.filmorate.model.MPA;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Repository
public class MPADbStorage implements MPAStorage {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public MPADbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<MPA> findAll() {
        String sql = "select MPA_ID, MPA_NAME from MPA";
        return jdbcTemplate.query(sql, this::mapRowToMPA);
    }

    private MPA mapRowToMPA(ResultSet rs, int rowNum) throws SQLException {
        Integer mpaId = rs.getInt("MPA_ID");
        String mpaName = rs.getString("MPA_NAME");
        return new MPA(mpaId, mpaName);
    }

    @Override
    public MPA create(MPA mpa) {
        SqlParameterSource namedParameters = new MapSqlParameterSource()
                .addValue("MPA_NAME", mpa.getName());
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("MPA")
                .usingGeneratedKeyColumns("MPA_ID");
        int mpaId = simpleJdbcInsert.executeAndReturnKey(namedParameters).intValue();
        mpa.setId(mpaId);
        return mpa;
    }

    @Override
    public MPA update(MPA mpa) {
        String sql = "update MPA set MPA_NAME = ? where MPA_ID = ?";
        int rowsAffected = jdbcTemplate.update(sql, mpa.getName(), mpa.getId());
        if (rowsAffected == 0) {
            throw new EntityNotFoundException("No entity MPA with id : " + mpa.getId());
        }
        return mpa;
    }

    @Override
    public void delete(int id) {
        String sql = "delete from MPA where MPA_ID = ?";
        int rowNum = jdbcTemplate.update(sql, id);
        if (rowNum == 0) {
            throw new EntityNotFoundException("No entity MPA with id : " + id);
        }
    }

    @Override
    public Optional<MPA> findById(int id) {
        String sql = "select MPA_ID, MPA_NAME from MPA where MPA_ID = ?";
        return jdbcTemplate.query(sql, this::mapRowToMPA, id).stream()
                .findFirst();
    }

}
