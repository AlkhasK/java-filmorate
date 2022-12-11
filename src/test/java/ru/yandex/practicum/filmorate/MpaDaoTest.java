package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.film.mpa.MpaStorage;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

public class MpaDaoTest extends AbstractDaoTest {

    @Autowired
    private MpaStorage mpaStorage;

    @Test
    void testFindByIdMPA() {
        Optional<Mpa> mpa = mpaStorage.findById(1);

        assertThat(mpa)
                .isPresent()
                .hasValueSatisfying(m -> assertThat(m)
                        .hasFieldOrPropertyWithValue("id", 1)
                );
    }

    @Test
    void testUpdateMPA() {
        Mpa mpa = mpaStorage.findById(1).orElse(new Mpa());

        mpa.setName("NEW");
        mpaStorage.update(mpa);

        assertThat(mpaStorage.findById(1))
                .isPresent()
                .hasValue(mpa);
    }

    @Test
    void testDeleteMPA() {
        mpaStorage.delete(1);

        assertThat(mpaStorage.findById(1)).isEmpty();
    }

    @Test
    void testFindAllMPA() {
        assertThat(mpaStorage.findAll())
                .hasSize(5)
                .extracting(Mpa::getId)
                .containsExactlyInAnyOrder(1, 2, 3, 4, 5);
    }

}
