package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import ru.yandex.practicum.filmorate.model.MPA;
import ru.yandex.practicum.filmorate.storage.film.mpa.MPAStorage;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class MPADaoTest extends AbstractDaoTest {

    @Autowired
    private MPAStorage mpaStorage;

    private MPA mpaForTest;

    @BeforeEach
    public void createMPAForTest() {
        mpaForTest = new MPA();
        mpaForTest.setName("D");
    }

    @Test
    void createMPA() {
        mpaForTest = mpaStorage.create(mpaForTest);

        Optional<MPA> createdMPA = mpaStorage.findById(mpaForTest.getId());
        assertTrue(createdMPA.isPresent());
        assertEquals(mpaForTest, createdMPA.get());
    }

    @Test
    void updateMPA() {
        mpaForTest = mpaStorage.create(mpaForTest);

        mpaForTest.setName("G");
        mpaForTest = mpaStorage.update(mpaForTest);

        Optional<MPA> updatedMPA = mpaStorage.findById(mpaForTest.getId());
        assertTrue(updatedMPA.isPresent());
        assertEquals(mpaForTest, updatedMPA.get());
    }

    @Test
    void deleteMPA() {
        mpaForTest = mpaStorage.create(mpaForTest);

        mpaStorage.delete(mpaForTest.getId());

        Optional<MPA> deletedMPA = mpaStorage.findById(mpaForTest.getId());
        assertTrue(deletedMPA.isEmpty());
    }

    @Test
    void findAllMPA() {
        MPA mpa1 = new MPA(1, "G");
        MPA mpa2 = new MPA(2, "PG");
        MPA mpa3 = new MPA(3, "PG-13");
        MPA mpa4 = new MPA(4, "R");
        MPA mpa5 = new MPA(5, "NC-17");

        List<MPA> mpa = mpaStorage.findAll();

        assertEquals(List.of(mpa1, mpa2, mpa3, mpa4, mpa5), mpa);
    }

    @Test
    void findByIdMPA() {
        mpaForTest = mpaStorage.create(mpaForTest);

        Optional<MPA> createdMPA = mpaStorage.findById(mpaForTest.getId());
        assertTrue(createdMPA.isPresent());
        assertEquals(mpaForTest, createdMPA.get());
    }
}
