package greencity.validator;

import greencity.exception.exceptions.BadSocialNetworkLinksException;
import java.util.List;
import javax.validation.ConstraintValidatorContext;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class SocialNetworkLinksValidatorTest {
    @InjectMocks
    SocialNetworkLinksValidator socialNetworkLinksValidator;
    @Mock
    ConstraintValidatorContext constraintValidatorContext;

    @Test
    void isValidWithWhenLinksNullReturnsTrueTest() {
        assertTrue(socialNetworkLinksValidator.isValid(null, constraintValidatorContext));
    }

    @Test
    void isValidWithTheSameLinksReturnsFalseTest() {
        List<String> links = List.of("https://example1.com", "https://example1.com");
        assertThrows(BadSocialNetworkLinksException.class,
            () -> socialNetworkLinksValidator.isValid(links, constraintValidatorContext));
    }

    @Test
    void isValidWithAllValidLinksReturnsTrueTest() {
        List<String> links = List.of("https://example1.com",
            "https://example2.com",
            "https://example3.com");
        assertTrue(socialNetworkLinksValidator.isValid(links, constraintValidatorContext));
    }

    @Test
    void isValidWithSixLinksThrowsExceptionTest() {
        List<String> links = List.of("https://example1.com",
            "https://example2.com",
            "https://example3.com",
            "https://example4.com",
            "https://example5.com",
            "https://example6.com");
        assertThrows(BadSocialNetworkLinksException.class,
            () -> socialNetworkLinksValidator.isValid(links, constraintValidatorContext));
    }
}
