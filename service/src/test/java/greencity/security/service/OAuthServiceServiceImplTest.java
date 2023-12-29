package greencity.security.service;

import greencity.ModelUtils;
import greencity.dto.user.UserVO;
import greencity.entity.User;
import greencity.enums.UserStatus;
import greencity.exception.exceptions.BadUserStatusException;
import greencity.exception.exceptions.UserBlockedException;
import greencity.repository.UserRepo;
import greencity.security.jwt.JwtTool;
import greencity.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.modelmapper.ModelMapper;

import java.util.HashMap;

import static greencity.ModelUtils.getUser;
import static greencity.ModelUtils.getUserVO;
import static org.junit.jupiter.api.Assertions.*;
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

    private final HashMap<String, Object> attributes = new HashMap<>() {{
        put("email", "test@email.com");
        put("name", "test");
        put("picture", "https://google.com");
    }};

    @Test
    void authenticate() {
        User user = getUser();
        when(userService.findByEmail(anyString())).thenReturn(getUserVO());
        when(userRepo.save(userRepo.save(user))).thenReturn(user);
        when(modelMapper.map(user, UserVO.class)).thenReturn(getUserVO());
        when(jwtTool.generateTokenKey()).thenReturn("access-token");
        oAuthServiceService.authenticate(attributes);
        verify(userService, times(1)).findByEmail(anyString());
        verify(userRepo, times(1)).save(any());
    }

    @Test
    void authenticateBlockedUser() {
        UserVO userVO = getUserVO();
        userVO.setUserStatus(UserStatus.BLOCKED);
        when(userService.findByEmail(anyString())).thenReturn(userVO);
        assertThrows(BadUserStatusException.class, () -> oAuthServiceService.authenticate(attributes));
    }

    @Test
    void authenticateDeactivatedUser() {
        UserVO userVO = getUserVO();
        userVO.setUserStatus(UserStatus.DEACTIVATED);
        when(userService.findByEmail(anyString())).thenReturn(userVO);
        assertThrows(BadUserStatusException.class, () -> oAuthServiceService.authenticate(attributes));
    }
}