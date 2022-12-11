package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.jdbc.Sql;
import ru.yandex.practicum.filmorate.model.Like;
import ru.yandex.practicum.filmorate.storage.film.like.LikeStorage;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

public class LikeDaoTest extends AbstractDaoTest {

    @Autowired
    @Qualifier("LikeDbStorage")
    private LikeStorage likeStorage;

    @Test
    @Sql("classpath:like/sql/data/create_data_for_likes.sql")
    void testFindByIdLike() {
        Optional<Like> like = likeStorage.findById(2, 1);

        assertThat(like).isPresent().hasValueSatisfying(l -> assertThat(l)
                .hasFieldOrPropertyWithValue("filmId", 2)
                .hasFieldOrPropertyWithValue("userId", 1)
        );
    }

    @Test
    @Sql("classpath:like/sql/data/create_data_for_likes.sql")
    void testCreateLike() {
        Like like = new Like(1, 1);

        likeStorage.create(like);

        Optional<Like> createdLike = likeStorage.findById(1, 1);
        assertThat(createdLike)
                .isPresent()
                .hasValue(like);
    }

    @Test
    @Sql("classpath:like/sql/data/create_data_for_likes.sql")
    void testDeleteLike() {
        Like likeForDelete = new Like(2, 1);

        likeStorage.delete(likeForDelete);

        assertThat(likeStorage.findById(2, 1)).isEmpty();
    }

    @Test
    @Sql("classpath:like/sql/data/create_data_for_likes.sql")
    void testFindByFilmIdLikes() {
        Like like1 = new Like(2, 1);
        Like like2 = new Like(2, 2);
        Like like3 = new Like(2, 3);

        assertThat(likeStorage.findByFilmId(2))
                .hasSize(3)
                .containsExactlyInAnyOrder(like1, like2, like3);
    }

    @Test
    @Sql("classpath:like/sql/data/create_data_for_likes.sql")
    void testFindAllLikes() {
        Like like1 = new Like(2, 1);
        Like like2 = new Like(2, 2);
        Like like3 = new Like(2, 3);
        Like like4 = new Like(3, 1);
        Like like5 = new Like(3, 2);
        Like like6 = new Like(3, 3);

        assertThat(likeStorage.findAll())
                .hasSize(6)
                .containsExactlyInAnyOrder(like1, like2, like3, like4, like5, like6);
    }

}
