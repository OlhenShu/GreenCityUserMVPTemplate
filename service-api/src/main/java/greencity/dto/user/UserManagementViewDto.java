package greencity.dto.user;

import lombok.*;

import javax.validation.constraints.Email;
import javax.validation.constraints.Pattern;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@Builder
public class UserManagementViewDto {
    @Pattern(regexp = "\\d+", message = "ID should contain only digits")
    private String id;
    private String name;
    @Email
    private String email;
    private String userCredo;
    private String role;
    @Pattern(regexp = "\\d+", message = "User status should contain only digits")
    private String userStatus;
}
