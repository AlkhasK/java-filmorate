package ru.yandex.practicum.filmorate.model;

import lombok.*;

import javax.validation.constraints.NotNull;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class Mpa {

    @NotNull
    private Integer id;
    @EqualsAndHashCode.Exclude
    private String name;

}
