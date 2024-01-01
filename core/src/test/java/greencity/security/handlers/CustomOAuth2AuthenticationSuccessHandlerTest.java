package greencity.security.handlers;

import greencity.security.service.OAuthServiceServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class CustomOAuth2AuthenticationSuccessHandlerTest {
    @Mock
    CustomOAuth2AuthenticationSuccessHandler customOAuth2AuthenticationSuccessHandler;
    @Mock
    private OAuthServiceServiceImpl oAuthService;


    private final HashMap<String, Object> attributes = new HashMap<>() {{
        put("email", "test@email.com");
        put("name", "test");
        put("picture", "https://google.com");
    }};

    @Test
    void onAuthenticationSuccess() throws IOException {
        MockHttpServletRequest mockHttpServletRequest = new MockHttpServletRequest();
        MockHttpServletResponse mockHttpServletResponse = new MockHttpServletResponse();
        Authentication auth = Mockito.mock(OAuth2AuthenticationToken.class);
        SecurityContext secCont = Mockito.mock(SecurityContext.class);
        Mockito.when(secCont.getAuthentication()).thenReturn(auth);
        when(auth.getPrincipal()).thenReturn(
                new DefaultOAuth2User(
                        new ArrayList<GrantedAuthority>(Collections.singleton(
                                new SimpleGrantedAuthority("ROLE_USER"))), attributes, "name"));
        customOAuth2AuthenticationSuccessHandler
                .onAuthenticationSuccess(mockHttpServletRequest, mockHttpServletResponse, auth);
        verify(customOAuth2AuthenticationSuccessHandler).onAuthenticationSuccess(mockHttpServletRequest, mockHttpServletResponse, auth);
    }
}