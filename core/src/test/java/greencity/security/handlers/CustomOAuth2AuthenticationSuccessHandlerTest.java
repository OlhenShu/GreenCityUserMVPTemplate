package greencity.security.handlers;

import greencity.security.service.OAuthServiceServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
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

import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class CustomOAuth2AuthenticationSuccessHandlerTest {
    @Spy
    @InjectMocks
    CustomOAuth2AuthenticationSuccessHandler customOAuth2AuthenticationSuccessHandler;
    @Mock
    private OAuthServiceServiceImpl oAuthService;

    private final HashMap<String, Object> attributes = new HashMap<>() {{
        put("email", "test@email.com");
        put("name", "test");
        put("picture", "https://lh3.googleusercontent.com/a/ACg8ocK2YyshyCq_qpPpNl0bS4xsQhY-I0NASRM7Hm-kIsYYEA=s96-c");
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
        verify(oAuthService).authenticate(attributes);
        verify(customOAuth2AuthenticationSuccessHandler)
                .onAuthenticationSuccess(mockHttpServletRequest, mockHttpServletResponse, auth);
    }

    @Test
    void notOAuthTokenTest() throws IOException {
        Authentication auth = Mockito.mock(Authentication.class);
        SecurityContext secCont = Mockito.mock(SecurityContext.class);
        MockHttpServletRequest mockHttpServletRequest = new MockHttpServletRequest();
        MockHttpServletResponse mockHttpServletResponse = new MockHttpServletResponse();
        Mockito.when(secCont.getAuthentication()).thenReturn(auth);
        customOAuth2AuthenticationSuccessHandler
                .onAuthenticationSuccess(mockHttpServletRequest, mockHttpServletResponse, auth);
        verify(oAuthService,times(0)).authenticate(anyMap());
        verify(customOAuth2AuthenticationSuccessHandler)
                .onAuthenticationSuccess(mockHttpServletRequest, mockHttpServletResponse, auth);
    }
}