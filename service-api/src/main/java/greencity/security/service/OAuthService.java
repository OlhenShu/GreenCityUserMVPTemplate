package greencity.security.service;

import greencity.security.dto.SuccessSignInDto;
import greencity.security.dto.SuccessSignUpDto;
import java.util.Map;

public interface OAuthService {


    SuccessSignInDto authorize(Map<String, Object> attributes);
}
