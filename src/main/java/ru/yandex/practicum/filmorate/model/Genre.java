package ru.yandex.practicum.filmorate.model;

import lombok.*;

import javax.validation.constraints.NotNull;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class Genre {

    @NotNull
    Integer id;
    @EqualsAndHashCode.Exclude
    String name;

}
