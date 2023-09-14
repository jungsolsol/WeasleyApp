package sol.server.common.jwt.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.GlobalMethodSecurityConfiguration;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import sol.server.common.jwt.entrypoint.EntryPointUnauthorizedHandler;
import sol.server.common.jwt.filter.JwtTokenFilter;
import sol.server.common.jwt.service.UserDetailsImpl;
import sol.server.common.jwt.service.UserDetailsServiceImpl;
import sol.server.common.jwt.util.JwtUtil;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import sol.server.core.repository.ProductRepository;
import sol.server.core.repository.UserRepository;
import sol.server.core.service.UserServiceImpl;

@EnableWebSecurity
@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final EntryPointUnauthorizedHandler entryPointUnauthorizedHandler;
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

//    private final UserDetailsImpl userDetails;
//    private final AuthenticationManagerBuilder authenticationManagerBuilder;

    @Bean
    public AuthenticationManager authenticationManagerBean()  {
        return new AuthenticationManager() {
            @Override
            public Authentication authenticate(Authentication authentication) throws AuthenticationException {
                return authentication;
            }
        };
    }
    //////
//    @Override
//    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
//        auth.userDetailsService(userDetailsService()).passwordEncoder(passwordEncoder());
//    }
    @Bean
    public UserDetailsService userDetailsService() {
        return new UserDetailsServiceImpl(userRepository, productRepository);
    }

    @Bean
    public JwtTokenFilter jwtTokenFilter(JwtUtil jwtUtil) {
        return new JwtTokenFilter(jwtUtil);
    }
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .httpBasic().disable()
                .csrf().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
//                .authorizeHttpRequests(authorizeRequests ->
//                        authorizeRequests

                .authorizeRequests()
                .requestMatchers("/api/auth", "/api/auth-e","/api/auth-a").permitAll()
//                .requestMatchers("/api/**").authenticated()
//                )
                .requestMatchers("/api/**").authenticated()
                .and()
                .addFilterBefore(jwtTokenFilter(jwtUtil), UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling()
                .authenticationEntryPoint(entryPointUnauthorizedHandler) // 인증되지 않은 사용자 접근 시
                .and()
                .build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}