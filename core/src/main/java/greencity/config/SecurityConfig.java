package greencity.config;

import greencity.security.filters.AccessTokenAuthenticationFilter;
import greencity.security.handlers.CustomOAuth2AuthenticationSuccessHandler;
import greencity.security.jwt.JwtTool;
import greencity.security.providers.JwtAuthenticationProvider;
import greencity.security.service.OAuthService;
import greencity.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.Collections;

import static greencity.constant.AppConstant.*;
import static javax.servlet.http.HttpServletResponse.*;

/**
 * Config for security.
 *
 * @author Nazar Stasyuk && Yurii Koval
 * @version 1.0
 */
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    private final JwtTool jwtTool;
    private final UserService userService;
    private final OAuthService oauthservice;
    private static final String USER_LINK = "/user";

    /**
     * Constructor.
     */

    @Autowired
    public SecurityConfig(JwtTool jwtTool, UserService userService,OAuthService oauthservice) {
        this.jwtTool = jwtTool;
        this.userService = userService;
        this.oauthservice = oauthservice;
    }

    /**
     * Bean {@link PasswordEncoder} that uses in coding password.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Method for configure security.
     *
     * @param http {@link HttpSecurity}
     */
    @Override
   protected void configure(HttpSecurity http) throws Exception {
        http.cors()
                .and()
                .csrf()
                .disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .addFilterBefore(
                        new AccessTokenAuthenticationFilter(jwtTool, authenticationManager(), userService),
                        UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling()
                .authenticationEntryPoint((req, resp, exc) -> resp.sendError(SC_UNAUTHORIZED, "Authorize first."))
                .accessDeniedHandler((req, resp, exc) -> resp.sendError(SC_FORBIDDEN, "You don't have authorities."))
                .and()
                .authorizeRequests()
                .antMatchers("/static/css/**",
                        "/static/img/**")
                .permitAll()
                .antMatchers(HttpMethod.GET,
                        "/ownSecurity/verifyEmail",
                        "/ownSecurity/updateAccessToken",
                        "/ownSecurity/restorePassword",
                        "/user/emailNotifications",
                        "/user/activatedUsersAmount",
                        "/user/{userId}/habit/assign",
                        "/token",
                        "/socket/**",
                        "/user/findAllByEmailNotification",
                        "/user/checkByUuid",
                        "/login/**",
                        "/oauth2/**")
                .permitAll()
                .antMatchers(HttpMethod.POST,
                        "/ownSecurity/signUp",
                        "/ownSecurity/signIn",
                        "/ownSecurity/updatePassword",
                        "/email/addEcoNews",
                        "/email/sendReport",
                        "/email/changePlaceStatus",
                        "/email/sendHabitNotification",
                        "/email/notification/newsSubscriber")
                .permitAll()
                .antMatchers(HttpMethod.GET,
                        USER_LINK,
                        "/user/shopping-list-items/habits/{habitId}/shopping-list",
                        "/user/{userId}/{habitId}/custom-shopping-list-items/available",
                        "/user/{userId}/profile/",
                        "/user/isOnline/{userId}/",
                        "/user/{userId}/profileStatistics/",
                        "/user/userAndSixFriendsWithOnlineStatus",
                        "/user/userAndAllFriendsWithOnlineStatus",
                        "/user/findNotDeactivatedByEmail",
                        "/user/findByEmail",
                        "/user/findIdByEmail",
                        "/user/findById",
                        "/user/findUserByName/**",
                        "/user/findByUuId",
                        "/user/findUuidByEmail",
                        "/user/lang",
                        "/user/createUbsRecord",
                        "/ownSecurity/password-status",
                        "/user/emailNotifications")
                .hasAnyRole(USER, ADMIN, UBS_EMPLOYEE, MODERATOR, EMPLOYEE)
                .antMatchers(HttpMethod.POST,
                        USER_LINK,
                        "/user/shopping-list-items",
                        "/user/{userId}/habit",
                        "/email/sendHabitNotification",
                        "/ownSecurity/set-password",
                        "/ownSecurity/set-password",
                        "/ownSecurity/set-password",
                        "/email/sendUserViolation",
                        "/email/changePlaceStatus")
                .hasAnyRole(USER, ADMIN, UBS_EMPLOYEE, MODERATOR, EMPLOYEE)
                .antMatchers(HttpMethod.PUT,
                        "/ownSecurity/changePassword",
                        "/user/profile",
                        "/user/{id}/updateUserLastActivityTime/{date}",
                        "/user/language/{languageId}",
                        "/user/employee-email")
                .hasAnyRole(USER, ADMIN, UBS_EMPLOYEE, MODERATOR, EMPLOYEE)
                .antMatchers(HttpMethod.PUT,
                        "/user/edit-authorities",
                        "/user/authorities",
                        "/user/deactivate-employee")
                .hasAnyRole(ADMIN, UBS_EMPLOYEE, MODERATOR, EMPLOYEE)
                .antMatchers(HttpMethod.GET,
                        "/user/get-all-authorities",
                        "/user/get-positions-authorities",
                        "/user/get-employee-login-positions")
                .hasAnyRole(ADMIN, UBS_EMPLOYEE, MODERATOR, EMPLOYEE)
                .antMatchers(HttpMethod.PATCH,
                        "/user/shopping-list-items/{userShoppingListItemId}",
                        "/user/profilePicture",
                        "/user/deleteProfilePicture")
                .hasAnyRole(USER, ADMIN, UBS_EMPLOYEE, MODERATOR, EMPLOYEE)
                .antMatchers(HttpMethod.DELETE,
                        "/user/shopping-list-items/user-shopping-list-items",
                        "/user/shopping-list-items")
                .hasAnyRole(USER, ADMIN, UBS_EMPLOYEE, MODERATOR, EMPLOYEE)
                .antMatchers(HttpMethod.GET,
                        "/user/all",
                        "/user/roles",
                        "/user/findUserForManagement",
                        "/user/searchBy",
                        "/user/findAll")
                .hasAnyRole(ADMIN, MODERATOR, EMPLOYEE)
                .antMatchers(HttpMethod.POST,
                        "/ownSecurity/sign-up-employee")
                .hasAnyRole(UBS_EMPLOYEE)
                .antMatchers(HttpMethod.POST,
                        "/user/filter",
                        "/ownSecurity/register")
                .hasAnyRole(ADMIN)
                .antMatchers(HttpMethod.PATCH,
                        "/user/status",
                        "/user/role",
                        "/user/update/role")
                .hasAnyRole(ADMIN)
                .antMatchers(HttpMethod.POST,
                        "/management/login")
                .not().fullyAuthenticated()
                .antMatchers(HttpMethod.GET,
                        "/management/login")
                .permitAll()
                .antMatchers("/css/**",
                        "/img/**")
                .permitAll()
                .anyRequest().hasAnyRole(ADMIN)
                .and()
                .oauth2Login()
                .successHandler(new CustomOAuth2AuthenticationSuccessHandler(oauthservice))
                .failureHandler((req, resp, exc) -> resp.sendError(SC_BAD_REQUEST, exc.getMessage()));
    }
  
    /**
     * Method for configure matchers that will be ignored in security.
     *
     * @param web {@link WebSecurity}
     */
    @Override
    public void configure(WebSecurity web) {
        web.ignoring().antMatchers("/v2/api-docs/**");
        web.ignoring().antMatchers("/swagger.json");
        web.ignoring().antMatchers("/swagger-ui.html");
        web.ignoring().antMatchers("/swagger-resources/**");
        web.ignoring().antMatchers("/webjars/**");
    }

    /**
     * Method for configure type of authentication provider.
     *
     * @param auth {@link AuthenticationManagerBuilder}
     */
    @Override
    protected void configure(AuthenticationManagerBuilder auth) {
        auth.authenticationProvider(new JwtAuthenticationProvider(jwtTool));
    }

    /**
     * Provides AuthenticationManager.
     *
     * @return {@link AuthenticationManager}
     */
    @Bean
    @Override
    public AuthenticationManager authenticationManager() throws Exception {
        return super.authenticationManager();
    }

    /**
     * Bean {@link CorsConfigurationSource} that uses for CORS setup.
     */
    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Collections.singletonList("*"));
        configuration.setAllowedMethods(
                Arrays.asList("GET", "POST", "OPTIONS", "DELETE", "PUT", "PATCH"));
        configuration.setAllowedHeaders(
                Arrays.asList(
                        "X-Requested-With", "Origin", "Content-Type", "Accept", "Authorization"));
        configuration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
