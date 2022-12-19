package ru.yandex.practicum.filmorate.model;

import lombok.*;

import javax.validation.constraints.NotNull;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class Friend {

    @NotNull
    private Integer userId;
    @NotNull
    private Integer friendId;
    @NotNull
    @EqualsAndHashCode.Exclude
    private Boolean confirmed;

}
