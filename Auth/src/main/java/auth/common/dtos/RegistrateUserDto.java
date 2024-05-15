package auth.common.dtos;

import lombok.Data;

@Data
public class RegistrateUserDto {
    private String username;
    private String password;
    private String firstName;
    private String lastName;
}
