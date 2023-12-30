package greencity.security.service;

import greencity.security.dto.SuccessSignInDto;

import java.util.Map;

/**
 * Provides the social authentication logic.
 *
 * @author Nikita Malov && Dmytro Klopov
 * @version 1.0
 */
public interface OAuthService {
    /**
     * Method that allow you to authenticate with social attributes.
     *
     * @param attributes {@link Map} - social attributes map.
     * @return {@link SuccessSignInDto}
     */
    SuccessSignInDto authenticate(Map<String, Object> attributes);
}
