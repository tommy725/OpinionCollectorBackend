package pl.opinion_collector.backend.logic.user.security;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import pl.opinion_collector.backend.logic.user.security.jwt.AuthEntryPointJwt;
import pl.opinion_collector.backend.logic.user.security.jwt.AuthTokenFilter;


@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfig {
    private static final String[][] ALL_USERS_PERMISSIONS = {
            {"/products",  "/categories"},
            {"/users/login", "/users/register",
                    "/products/search", "/products/details",
                    "/products/{page}", "/opinions/product"}
    };
    private static final String[] STD_USER_PERMISSIONS = {
            "/suggestions/user", "/suggestions/add",
            "/opinions/user", "/opinions/add"
    };
    private static final String[][] ADMIN_PERMISSIONS = {
            {"/users"},
            {"/users/update", "/products/all", "/products/add", "/products/edit", "/products/delete",
            "/categories/all", "/categories/add",
            "/categories/edit", "/categories/delete",
            "/suggestions/get", "/suggestions/reply"},
            {"/products/all/{page}"}
    };
    private static final String[][] SWAGGER_WHITELIST = {
            {"/**/swagger-ui/**", "/**/swagger-resources/**",
                    "/**/swagger-resources/**", "/**/swagger-ui.html/**",
                    "/**/webjars/**", "/**/swagger-ui/**"},
            { "/**/swagger-ui/index.html/**",
                    "/**/v2/api-docs/**", "/**/configuration/ui/**",
                    "/**/configuration/security/**", "/**/v3/api-docs/**"}
    };

    private static final String ADMIN_ROLE = "ADMIN";
    private static final String USER_ROLE = "USER";

    @Bean
    public AuthEntryPointJwt getUnauthorizedHandler() {
        return new AuthEntryPointJwt();
    }

    @Bean
    public AuthTokenFilter authenticationJwtTokenFilter() {
        return new AuthTokenFilter();
    }

    @Bean
    public AuthenticationManager authenticationManagerBean(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    protected SecurityFilterChain configure(HttpSecurity http) throws Exception {
        http
                .cors().and().csrf().disable()
                .exceptionHandling().authenticationEntryPoint(getUnauthorizedHandler())
                .and()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
                .authorizeRequests()
                    .antMatchers(ADMIN_PERMISSIONS[2]).hasRole(ADMIN_ROLE)
                    .antMatchers(ADMIN_PERMISSIONS[1]).hasRole(ADMIN_ROLE)
                    .antMatchers(STD_USER_PERMISSIONS).hasRole(USER_ROLE)
                    .antMatchers(ALL_USERS_PERMISSIONS[1]).permitAll()
                    .antMatchers(SWAGGER_WHITELIST[1]).permitAll()
                    .antMatchers(ADMIN_PERMISSIONS[0]).hasRole(ADMIN_ROLE)
                    .antMatchers(SWAGGER_WHITELIST[0]).permitAll()
                    .antMatchers(ALL_USERS_PERMISSIONS[0]).permitAll()
                    .antMatchers("/*").permitAll()
                .anyRequest().authenticated()
                .and().addFilterBefore(authenticationJwtTokenFilter(), UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

}