package ru.d1g.doceasy.core.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.RememberMeAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.RememberMeServices;
import org.springframework.security.web.authentication.logout.HttpStatusReturningLogoutSuccessHandler;
import org.springframework.security.web.authentication.rememberme.RememberMeAuthenticationFilter;
import org.springframework.security.web.authentication.rememberme.TokenBasedRememberMeServices;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import ru.d1g.doceasy.core.Constants;
import ru.d1g.doceasy.core.service.UserParticularityService;

import javax.servlet.http.HttpServletResponse;

@Configuration
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    private static final String LOGIN_PROCESSING_URL = "/login";
    private static final String rememberMeKey = "jOq8C71D(1]]Ck.";

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
    public void configure(WebSecurity web) throws Exception {
        web.ignoring()
                .antMatchers(
                        "/favicon.ico",
                        "/apple-touch-icon-precomposed.png",
                        "/apple-touch-icon-76x76-precomposed.png",
                        "/apple-touch-icon-120x120-precomposed.png",
                        "/apple-touch-icon-152x152-precomposed.png"
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
                .exceptionHandling().authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED))
                .and()
                .authorizeRequests()
                .antMatchers(
                        Constants.API_URL+"**",
                        "/swagger-ui/**", "/v3/api-docs/**", "/swagger-ui.html", "/actuator/**",
                        "/registration/**",
                        "/support/**"
                ).permitAll()
                .anyRequest().authenticated()
                .and()
//                .addFilterBefore(jsonAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class)
//                .addFilterAfter(rememberMeAuthenticationFilter(), JsonAuthenticationFilter.class)
                .logout()
                .logoutUrl("/logout")
                .logoutSuccessHandler((new HttpStatusReturningLogoutSuccessHandler(HttpStatus.OK))).permitAll()
                .and().rememberMe().rememberMeServices(rememberMeServices());
    }
}
