package greencity.security.service;

import greencity.dto.user.UserVO;
import greencity.entity.Language;
import greencity.enums.EmailNotification;
import greencity.enums.Role;
import greencity.enums.UserStatus;
import greencity.mapping.UserVOMapper;
import greencity.repository.UserRepo;
import greencity.security.dto.SuccessSignInDto;
import greencity.security.jwt.JwtTool;
import greencity.service.UserService;
import java.time.LocalDateTime;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import greencity.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
@Slf4j
public class OAuthServiceServiceImpl implements OAuthService {
    private final JwtTool jwtTool;
    private final UserService userService;
    private final UserRepo userRepo;
    private final ModelMapper modelMapper;

    @Override
    @Transactional
    public SuccessSignInDto authorize(Map<String, Object> attributes) {
        String email = attributes.get("email").toString();
        String userName =  attributes.get("name").toString();
        UserVO userVO = userService.findByEmail(email);
        if (userVO == null) {
            User newUser = createNewUser(email, userName);
            var savedUser = userRepo.save(newUser);
            userVO = modelMapper.map(savedUser, UserVO.class);
            log.info("Facebook/Google sign-up exist user - {}", userVO.getEmail());
            return getSuccessSignInDto(userVO);
        } else {
            log.info("Facebook/Google sign-in exist user - {}", userVO.getEmail());
            return getSuccessSignInDto(userVO);
        }
    }

    private User createNewUser(String email, String userName) {
        return User.builder()
            .email(email)
            .name(userName)
            .role(Role.ROLE_USER)
            .uuid(UUID.randomUUID().toString())
            .dateOfRegistration(LocalDateTime.now())
            .lastActivityTime(LocalDateTime.now())
            .userStatus(UserStatus.ACTIVATED)
            .emailNotification(EmailNotification.DISABLED)
            .language(Language.builder()
                .id(modelMapper.map(Locale.getDefault().getLanguage(), Long.class))
                .build())
            .refreshTokenKey(jwtTool.generateTokenKey())
            .build();
    }

    private SuccessSignInDto getSuccessSignInDto(UserVO user) {
        String accessToken = jwtTool.createAccessToken(user.getEmail(), user.getRole());
        String refreshToken = jwtTool.createRefreshToken(user);
        return new SuccessSignInDto(user.getId(), accessToken, refreshToken, user.getName(), false);
    }
}
