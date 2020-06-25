package ru.d1g.doceasy.core.config;

import com.vaadin.flow.server.ServletHelper;
import com.vaadin.flow.shared.ApplicationConstants;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.RememberMeAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.RememberMeServices;
import org.springframework.security.web.authentication.rememberme.RememberMeAuthenticationFilter;
import org.springframework.security.web.authentication.rememberme.TokenBasedRememberMeServices;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import ru.d1g.doceasy.core.Constants;
import ru.d1g.doceasy.core.service.UserParticularityService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.stream.Stream;

@Configuration
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    private static final String LOGIN_PROCESSING_URL = "/start/login";
    private static final String LOGIN_SUCCESS_URL = "/start/";
    private static final String LOGIN_FAILURE_URL = "/start/login?error";
    private static final String LOGIN_URL = "/start/login";
    private static final String LOGOUT_SUCCESS_URL = "/start/login";

    private static final String rememberMeKey = "i9So1kSk..c]@k";

    private final UserParticularityService userParticularityService;

    public WebSecurityConfig(UserParticularityService userParticularityService) {
        this.userParticularityService = userParticularityService;
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return authenticationManager();
    }

    @Bean
    public RememberMeServices rememberMeServices() {
        TokenBasedRememberMeServices tokenBasedRememberMeServices = new TokenBasedRememberMeServices(rememberMeKey, userParticularityService);
        tokenBasedRememberMeServices.setAlwaysRemember(true);
        tokenBasedRememberMeServices.setCookieName("dsy");
        tokenBasedRememberMeServices.setTokenValiditySeconds(60 * 60 * 24 * 360);
        return tokenBasedRememberMeServices;
    }

    @Bean
    public RememberMeAuthenticationFilter rememberMeAuthenticationFilter() throws Exception {
        return new RememberMeAuthenticationFilter(authenticationManager(), rememberMeServices());
    }

    @Bean
    public RememberMeAuthenticationProvider rememberMeAuthenticationProvider() {
        return new RememberMeAuthenticationProvider(rememberMeKey);
    }

    @Bean
    public AuthenticationEntryPoint unauthorizedEntryPoint() {
        return (request, response, authException) -> response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userParticularityService);
        auth.authenticationProvider(rememberMeAuthenticationProvider());
    }

    @Override
    public void configure(WebSecurity web) {
        web.ignoring()
                .antMatchers(
                        "/favicon.ico",
                        "/apple-touch-icon-precomposed.png",
                        "/apple-touch-icon-76x76-precomposed.png",
                        "/apple-touch-icon-120x120-precomposed.png",
                        "/apple-touch-icon-152x152-precomposed.png",
                        "/start/VAADIN/**",
                        "/VAADIN/**",
                        "/favicon.ico",
                        "/robots.txt",
                        "/manifest.webmanifest",
                        "/sw.js",
                        "/offline-page.html",
                        "/icons/**",
                        "/images/**",
                        "/frontend/**",
                        "/webjars/**",
                        "/frontend-es5/**", "/frontend-es6/**"
                );
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        HttpSessionRequestCache sessionRequestCache = new HttpSessionRequestCache();

        http.csrf().disable()
                .cors()
                .and()
                .requestCache().requestCache(sessionRequestCache)
                .and()
                .authorizeRequests()
                .requestMatchers(WebSecurityConfig::isFrameworkInternalRequest).permitAll()
                .antMatchers(LOGIN_URL, "/start/registration", "/registration", "/registration/**").permitAll()
                .antMatchers(
                        Constants.API_URL + "**",
                        "/swagger-ui/**", "/v3/api-docs/**", "/swagger-ui.html", "/actuator/**",
                        "/support/**"
                ).permitAll()
                .anyRequest().authenticated()
                .and()
                .formLogin()
                .loginPage(LOGIN_URL)
                .loginProcessingUrl(LOGIN_PROCESSING_URL)
                .successForwardUrl(LOGIN_SUCCESS_URL)
                .failureForwardUrl(LOGIN_FAILURE_URL)
                .and()
                .logout().logoutSuccessUrl(LOGOUT_SUCCESS_URL)
                .and().rememberMe().rememberMeServices(rememberMeServices());
    }

    static boolean isFrameworkInternalRequest(HttpServletRequest request) {
        final String parameterValue = request.getParameter(ApplicationConstants.REQUEST_TYPE_PARAMETER);
        return parameterValue != null
                && Stream.of(ServletHelper.RequestType.values()).anyMatch(r -> r.getIdentifier().equals(parameterValue));
    }
}
