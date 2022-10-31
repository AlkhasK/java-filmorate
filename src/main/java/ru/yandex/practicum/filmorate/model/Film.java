package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;
import lombok.NonNull;
import ru.yandex.practicum.filmorate.model.serializer.DurationSerializer;

import java.time.Duration;
import java.time.LocalDate;

@Data
public class Film {

    private Integer id;
    @NonNull
    private String name;
    @NonNull
    private String description;
    @NonNull
    private LocalDate releaseDate;
    @NonNull
    @JsonSerialize(using = DurationSerializer.class)
    private Duration duration;

}
