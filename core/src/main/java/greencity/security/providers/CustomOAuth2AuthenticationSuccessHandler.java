package greencity.security.providers;

import com.fasterxml.jackson.databind.ObjectMapper;
import greencity.security.dto.SuccessSignInDto;
import greencity.security.service.OAuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

@RequiredArgsConstructor
@Component
@Slf4j
public class CustomOAuth2AuthenticationSuccessHandler extends
    SimpleUrlAuthenticationSuccessHandler {

    private final OAuthService oAuthService;

    @Override
    public void onAuthenticationSuccess(
        HttpServletRequest request,
        HttpServletResponse response,
        Authentication authentication) throws IOException {

        if(authentication instanceof OAuth2AuthenticationToken){
            OAuth2User oauth2User = ((OAuth2AuthenticationToken) authentication).getPrincipal();
            Map<String, Object> attributes = oauth2User.getAttributes();
            SuccessSignInDto successSignInDto = oAuthService.authenticate(attributes);
            ObjectMapper objectMapper = new ObjectMapper();
            String json = objectMapper.writeValueAsString(successSignInDto);
            response.setContentType("application/json");
            response.getWriter().write(json);
            response.getWriter().flush();
        }else{
            logger.error("Failed to login with OAuth2AuthenticationToken");
        }

    }
}
