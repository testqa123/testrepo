package com.aurea.zbw.api.config;

import static javax.servlet.http.HttpServletResponse.SC_UNAUTHORIZED;

import com.aurea.zbw.api.ApiProperties;
import com.aurea.zbw.api.controllers.ForgotPasswordController;
import com.aurea.zbw.api.security.JWTAuthenticationFilter;
import com.aurea.zbw.api.security.JWTLoginFilter;
import com.aurea.zbw.api.security.MyUserDetailsService;
import com.aurea.zbw.api.security.TokenAuthenticationService;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    private static final String ROOT_PATH = "/";

    @Value("${springfox.documentation.swagger.v2.path}")
    private String apiDocsPath;

    @Value("${endpoints.health.path}")
    private String endpointsHealthPath;

    @Autowired
    private JWTAuthenticationFilter jWTAuthenticationFilter;

    @Autowired
    private TokenAuthenticationService tokenAuthenticationService;

    @Autowired
    private MyUserDetailsService userDetailsService;

    @Autowired
    private ApiProperties properties;

    @Override
    protected final void configure(final HttpSecurity http) throws Exception {
        http
            .csrf().disable()
                .exceptionHandling()
                    .authenticationEntryPoint(authenticationEntryPoint())
            .and()
                .authorizeRequests()
                // Allowing Swagger-UI to Work
                .antMatchers(HttpMethod.GET, ROOT_PATH).permitAll()
                .antMatchers(HttpMethod.GET, properties.getApi().getSwagger().getUiPath()).permitAll()
                .antMatchers(HttpMethod.GET, properties.getApi().getSwagger().getResourcesPath1()).permitAll()
                .antMatchers(HttpMethod.GET, properties.getApi().getSwagger().getResourcesPath2()).permitAll()
                .antMatchers(HttpMethod.GET, apiDocsPath).permitAll()
                .antMatchers(HttpMethod.POST, ForgotPasswordController.ENDPOINT).permitAll()
                // Allowing Actuator Health
                .antMatchers(HttpMethod.GET, endpointsHealthPath).permitAll()
                // Allowing Authenticate Endpoint
                .antMatchers(HttpMethod.POST, properties.getApi().getSecurity().getAuthentication().getLoginPath()).permitAll()
                // Allowing Specific Endpoints
                .anyRequest().authenticated()
            .and()
                // We filter the /login requests
                .addFilterBefore(jwtLoginFilter(), UsernamePasswordAuthenticationFilter.class)
                // And filter other requests to check the presence of JWT in header
                .addFilterBefore(jWTAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
    }

    @Autowired
    @Override
    public final void configure(final AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(authenticationProvider());
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        return authProvider;
    }

    @Bean
    public JWTLoginFilter jwtLoginFilter() throws Exception {
        return new JWTLoginFilter(properties, authenticationManager(), tokenAuthenticationService);
    }

    private AuthenticationEntryPoint authenticationEntryPoint() {
        return (HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) ->
            response.sendError(SC_UNAUTHORIZED, "Unauthorized");
    }

}
