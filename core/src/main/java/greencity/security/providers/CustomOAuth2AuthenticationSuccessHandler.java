package greencity.security.providers;

import com.fasterxml.jackson.databind.ObjectMapper;
import greencity.constant.ErrorMessage;
import greencity.dto.user.UserVO;
import greencity.enums.EmailNotification;
import greencity.enums.Role;
import greencity.enums.UserStatus;
import greencity.exception.exceptions.UserAlreadyRegisteredException;
import greencity.security.dto.SuccessSignInDto;
import greencity.security.dto.SuccessSignUpDto;
import greencity.security.jwt.JwtTool;
import greencity.security.service.OAuthService;
import greencity.service.UserService;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class CustomOAuth2AuthenticationSuccessHandler extends
    SimpleUrlAuthenticationSuccessHandler {

    private final OAuthService oAuthService;

    @Override
    public void onAuthenticationSuccess(
        HttpServletRequest request,
        HttpServletResponse response,
        Authentication authentication) throws IOException,
        ServletException {

        if(authentication instanceof OAuth2AuthenticationToken){
            OAuth2User oauth2User = ((OAuth2AuthenticationToken) authentication).getPrincipal();
            Map<String, Object> attributes = oauth2User.getAttributes();
            SuccessSignInDto successSignInDto = oAuthService.authorize(attributes);
            ObjectMapper objectMapper = new ObjectMapper();
            String json = objectMapper.writeValueAsString(successSignInDto);
            response.setContentType("application/json");
            response.getWriter().write(json);
            response.getWriter().flush();
        }else{
            //error
        }

    }
}
