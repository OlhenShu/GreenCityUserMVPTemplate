package greencity.message;

import java.io.Serializable;
import javax.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * Message, that is used for sending emails about not marked habits.
 */
@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class SendHabitNotification implements Serializable {
    private String name;
    @Email
    private String email;
}
