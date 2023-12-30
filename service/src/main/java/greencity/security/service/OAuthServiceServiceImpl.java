package greencity.security.service;

import greencity.constant.ErrorMessage;
import greencity.dto.user.UserVO;
import greencity.entity.Language;
import greencity.entity.User;
import greencity.enums.EmailNotification;
import greencity.enums.Role;
import greencity.enums.UserStatus;
import greencity.exception.exceptions.BadUserStatusException;
import greencity.repository.UserRepo;
import greencity.security.dto.SuccessSignInDto;
import greencity.security.jwt.JwtTool;
import greencity.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static greencity.constant.AppConstant.DEFAULT_RATING;

/**
 * {@inheritDoc}
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class OAuthServiceServiceImpl implements OAuthService {
    private final JwtTool jwtTool;
    private final UserService userService;
    private final UserRepo userRepo;
    private final ModelMapper modelMapper;

    /**
     * {@inheritDoc}
     */
    @Override
    public SuccessSignInDto authenticate(Map<String, Object> attributes) {
        String email = attributes.get("email").toString();
        String userName = attributes.get("name").toString();
        String picture = attributes.get("picture").toString();

        if (picture.contains("url")) {
            Pattern pattern = Pattern.compile("url=([^,}]*)");
            Matcher matcher = pattern.matcher(picture);
            if (matcher.find()) {
                picture = matcher.group(1).trim();
            }
        }

        UserVO userVO = userService.findByEmail(email);
        if (userVO == null) {
            User newUser = createNewUser(email, userName, picture);
            newUser.setShowLocation(true);
            newUser.setShowEcoPlace(true);
            newUser.setShowShoppingList(true);
            var savedUser = userRepo.save(newUser);
            userVO = modelMapper.map(savedUser, UserVO.class);
            log.info("Facebook/Google sign-up non existed user - {}", userVO.getEmail());
            return getSuccessSignInDto(userVO);
        } else {
            if (userVO.getUserStatus() == UserStatus.DEACTIVATED) {
                throw new BadUserStatusException(ErrorMessage.USER_DEACTIVATED);
            }
            if (userVO.getUserStatus() == UserStatus.BLOCKED) {
                throw new BadUserStatusException(ErrorMessage.USER_BLOCKED);
            }
            log.info("Facebook/Google sign-in existed user - {}", userVO.getEmail());
            return getSuccessSignInDto(userVO);
        }
    }

    private User createNewUser(String email, String userName, String picture) {
        return User.builder()
            .email(email)
            .name(userName)
            .role(Role.ROLE_USER)
            .uuid(UUID.randomUUID().toString())
            .rating(DEFAULT_RATING)
            .profilePicturePath(picture)
            .dateOfRegistration(LocalDateTime.now())
            .lastActivityTime(LocalDateTime.now())
            .userStatus(UserStatus.ACTIVATED)
            .emailNotification(EmailNotification.DISABLED)
            .language(Language.builder()
                .id(modelMapper.map(Locale.ENGLISH.toString(), Long.class))
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
