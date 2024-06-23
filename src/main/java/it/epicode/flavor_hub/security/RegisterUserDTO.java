package it.epicode.flavor_hub.security;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;


@Data
@AllArgsConstructor
@Builder(setterPrefix = "with")
public class RegisterUserDTO {
    @NotEmpty
    String firstName;
    @NotEmpty
    String lastName;
    @NotEmpty
    String username;
    @NotEmpty
    String email;
    @NotEmpty
    String password;

    String avatar;
}