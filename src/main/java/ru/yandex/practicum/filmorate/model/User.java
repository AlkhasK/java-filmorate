package ru.yandex.practicum.filmorate.model;

import lombok.*;

import javax.validation.constraints.*;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class User {

    private Integer id;
    @NotBlank
    @Email
    private String email;
    @NotBlank
    @Pattern(regexp = "^[\\S]*$")
    private String login;
    private String name;
    @NotNull
    @PastOrPresent
    private LocalDate birthday;

}
