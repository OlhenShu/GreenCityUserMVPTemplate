package greencity.security.service;

import greencity.security.dto.SuccessSignInDto;

import java.util.Map;

public interface OAuthService {
    SuccessSignInDto authenticate(Map<String, Object> attributes);
}
