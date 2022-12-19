package ru.yandex.practicum.filmorate.storage.film.mpa;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.controller.exception.EntityNotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Repository
public class MpaDbStorage implements MpaStorage {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public MpaDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Mpa> findAll() {
        String sql = "select MPA_ID, MPA_NAME from MPA";
        return jdbcTemplate.query(sql, this::mapRowToMPA);
    }

    private Mpa mapRowToMPA(ResultSet rs, int rowNum) throws SQLException {
        Integer mpaId = rs.getInt("MPA_ID");
        String mpaName = rs.getString("MPA_NAME");
        return new Mpa(mpaId, mpaName);
    }

    @Override
    public Mpa create(Mpa mpa) {
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
    public Mpa update(Mpa mpa) {
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
    public Optional<Mpa> findById(int id) {
        String sql = "select MPA_ID, MPA_NAME from MPA where MPA_ID = ?";
        return jdbcTemplate.query(sql, this::mapRowToMPA, id).stream()
                .findFirst();
    }

}
