package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.jdbc.Sql;
import ru.yandex.practicum.filmorate.model.Like;
import ru.yandex.practicum.filmorate.storage.film.like.LikeStorage;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class LikeDaoTest extends AbstractDaoTest {

    @Autowired
    @Qualifier("LikeDbStorage")
    private LikeStorage likeStorage;

    private Like likeForTest;

    @BeforeEach
    public void createLikeForTest() {
        likeForTest = new Like(1, 1);
    }

    @Test
    @Sql("classpath:like/sql/data/create_data_for_likes.sql")
    void createLike() {
        likeStorage.create(likeForTest);

        Optional<Like> createdLike = likeStorage.findById(likeForTest.getFilmId(), likeForTest.getUserId());
        assertTrue(createdLike.isPresent());
        assertEquals(likeForTest, createdLike.get());
    }

    @Test
    @Sql("classpath:like/sql/data/create_data_for_likes.sql")
    void deleteLike() {
        likeStorage.create(likeForTest);
        likeStorage.delete(likeForTest);

        Optional<Like> deletedLike = likeStorage.findById(likeForTest.getFilmId(), likeForTest.getUserId());
        assertTrue(deletedLike.isEmpty());
    }

    @Test
    @Sql("classpath:like/sql/data/create_data_for_likes.sql")
    void findByFilmIdLikes() {
        Like like1 = new Like(2, 1);
        Like like2 = new Like(2, 2);
        Like like3 = new Like(2, 3);

        List<Like> likes = likeStorage.findByFilmId(like1.getFilmId());

        assertEquals(List.of(like1, like2, like3), likes);
    }

    @Test
    @Sql("classpath:like/sql/data/create_data_for_likes.sql")
    void findAllLikes() {
        Like like1 = new Like(2, 1);
        Like like2 = new Like(2, 2);
        Like like3 = new Like(2, 3);
        Like like4 = new Like(3, 1);
        Like like5 = new Like(3, 2);
        Like like6 = new Like(3, 3);

        List<Like> likes = likeStorage.findAll();

        assertEquals(List.of(like1, like2, like3, like4, like5, like6), likes);
    }

    @Test
    @Sql("classpath:like/sql/data/create_data_for_likes.sql")
    void findByIdLike() {
        Like likeExp = new Like(2, 1);

        Optional<Like> like = likeStorage.findById(likeExp.getFilmId(), likeExp.getUserId());

        assertTrue(like.isPresent());
        assertEquals(likeExp, like.get());
    }
}
