package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.*;
import ru.yandex.practicum.filmorate.model.serializer.DurationSerializer;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.Duration;
import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Builder
@AllArgsConstructor
@EqualsAndHashCode
public class Film {

    private Integer id;
    @NotBlank
    private String name;
    @NotNull
    @Size(max = 200)
    private String description;
    @NotNull
    private LocalDate releaseDate;
    @NotNull
    @JsonSerialize(using = DurationSerializer.class)
    private Duration duration;
    private List<Genre> genres;
    private MPA mpa;
}
