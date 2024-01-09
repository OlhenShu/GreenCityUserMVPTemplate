package greencity.security.service;

import greencity.dto.user.UserVO;
import greencity.entity.User;
import greencity.enums.UserStatus;
import greencity.exception.exceptions.BadUserStatusException;
import greencity.repository.UserRepo;
import greencity.security.dto.SuccessSignInDto;
import greencity.security.jwt.JwtTool;
import greencity.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.modelmapper.ModelMapper;

import java.util.HashMap;

import static greencity.ModelUtils.getUser;
import static greencity.ModelUtils.getUserVO;
import static greencity.enums.Role.ROLE_USER;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class OAuthServiceServiceImplTest {
    @Mock
    private JwtTool jwtTool;
    @Mock
    private UserService userService;
    @Mock
    private UserRepo userRepo;
    @Mock
    private ModelMapper modelMapper;
    @InjectMocks
    private OAuthServiceServiceImpl oAuthServiceService;

    private final HashMap<String, Object> attributesForGoogleAuth = new HashMap<>() {{
        put("email", "test@email.com");
        put("name", "test");
        put("picture", "https://lh3.googleusercontent.com/a/ACg8ocK2YyshyCq_qpPpNl0bS4xsQhY-I0NASRM7Hm-kIsYYEA=s96-c");
    }};

    private final HashMap<String, Object> attributesForFacebookAuth = new HashMap<>() {{
        put("email", "test@email.com");
        put("name", "test");
        put("picture", "{data={height=50, is_silhouette=true, " +
                "url=https://scontent-iev1-1.xx.fbcdn.net/v/t1.30497-1/84628273_176159830277856_972693363922829312_" +
                "n.jpg?stp=c15.0.50.50a_cp0_dst-jpg_p50x50&_nc_cat=1&ccb=1-7&_nc_sid=810bd0&_nc_ohc=zAuT-" +
                "GMhk3cAX_RAgqh&_nc_ht=scontent-iev1-1.xx&edm=AP4hL3IEAAAA&oh=00_AfCXbfLkTDSDMld-ipz7b0DNPmKBAx" +
                "nPOlLDB5sVNxyoaw&oe=65BB99D9, width=50}}");
    }};

    @Test
    void authenticateGoogleOAuth() {
        User user = getUser();
        when(userService.findByEmail(anyString())).thenReturn(getUserVO());
        when(userRepo.save(any(User.class))).thenReturn(user);
        when(modelMapper.map(user, UserVO.class)).thenReturn(getUserVO());
        String accessToken = "access-token";
        when(jwtTool.createAccessToken("taras@gmail.com", ROLE_USER)).thenReturn(accessToken);
        String refreshToken = "refresh-token";
        when(jwtTool.createRefreshToken(any())).thenReturn(refreshToken);
        var actual = oAuthServiceService.authenticate(attributesForGoogleAuth);
        var expected = new SuccessSignInDto(user.getId(), accessToken, refreshToken, user.getName(), false);
        assertEquals(actual,expected);
        verify(userService, times(1)).findByEmail(anyString());
    }


    @Test
    void authenticateFacebookOAuth() {
        User user = getUser();
        when(userService.findByEmail(anyString())).thenReturn(null);
        when(userRepo.save(any(User.class))).thenReturn(user);
        when(modelMapper.map(user, UserVO.class)).thenReturn(getUserVO());
        String accessToken = "access-token";
        when(jwtTool.createAccessToken("taras@gmail.com", ROLE_USER)).thenReturn(accessToken);
        String refreshToken = "refresh-token";
        when(jwtTool.createRefreshToken(any())).thenReturn(refreshToken);
        var actual = oAuthServiceService.authenticate(attributesForFacebookAuth);
        var expected = new SuccessSignInDto(user.getId(), accessToken, refreshToken, user.getName(), false);
        assertEquals(actual,expected);
        verify(userService, times(1)).findByEmail(anyString());
    }

    @Test
    void authenticateBlockedUser() {
        UserVO userVO = getUserVO();
        userVO.setUserStatus(UserStatus.BLOCKED);
        when(userService.findByEmail(anyString())).thenReturn(userVO);
        assertThrows(BadUserStatusException.class,
                () -> oAuthServiceService.authenticate(attributesForGoogleAuth));
    }

    @Test
    void authenticateDeactivatedUser() {
        UserVO userVO = getUserVO();
        userVO.setUserStatus(UserStatus.DEACTIVATED);
        when(userService.findByEmail(anyString())).thenReturn(userVO);
        assertThrows(BadUserStatusException.class,
                () -> oAuthServiceService.authenticate(attributesForGoogleAuth));
    }
}