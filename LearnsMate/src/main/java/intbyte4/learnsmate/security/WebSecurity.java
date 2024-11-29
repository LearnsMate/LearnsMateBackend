package intbyte4.learnsmate.security;

import intbyte4.learnsmate.admin.service.AdminService;
import jakarta.servlet.Filter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.Collections;

@Configuration
@EnableWebSecurity
public class WebSecurity {

    private BCryptPasswordEncoder bCryptPasswordEncoder;
    private final RedisTemplate<String, String> redisTemplate;
    private AdminService userService;
    private Environment env;
    private JwtUtil jwtUtil;

    @Autowired
    public WebSecurity(BCryptPasswordEncoder bCryptPasswordEncoder, AdminService userService, Environment env, JwtUtil jwtUtil, RedisTemplate<String, String> redisTemplate) {
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.userService = userService;
        this.env = env;
        this.jwtUtil = jwtUtil;
        this.redisTemplate = redisTemplate;
    }


    /* 인가(Authorization)용 메소드 */
    @Bean
    protected SecurityFilterChain configure(HttpSecurity http) throws Exception {
        // CSRF 비활성화
        http.csrf(csrf -> csrf.disable());
        // CORS 설정 적용
        http.cors(cors -> cors.configurationSource(corsConfigurationSource()));

        // 로그인 시 추가할 authenticationManager 설정
        AuthenticationManagerBuilder authenticationManagerBuilder =
                http.getSharedObject(AuthenticationManagerBuilder.class);
        authenticationManagerBuilder.userDetailsService(userService)
                .passwordEncoder(bCryptPasswordEncoder);

        AuthenticationManager authenticationManager = authenticationManagerBuilder.build();

        // HttpSecurity 설정
        http.authorizeHttpRequests((authz) ->
                        authz
                                .requestMatchers(new AntPathRequestMatcher("/error")).permitAll()
                                .requestMatchers(new AntPathRequestMatcher("/swagger-ui/index.html", "GET")).permitAll()
                                .requestMatchers(new AntPathRequestMatcher("/swagger-ui/**", "GET")).permitAll()
                                .requestMatchers(new AntPathRequestMatcher("/v3/api-docs/**", "GET")).permitAll()
                                .requestMatchers(new AntPathRequestMatcher("/admin/**","GET")).permitAll()
                                .requestMatchers(new AntPathRequestMatcher("/auth/**","GET")).permitAll()
                                .requestMatchers(new AntPathRequestMatcher("/auth/**","POST")).permitAll()
                                .requestMatchers(new AntPathRequestMatcher("/admin/**","POST")).permitAll()
                                .requestMatchers(new AntPathRequestMatcher("/users/**", "POST")).permitAll()
                                .requestMatchers(new AntPathRequestMatcher("/users/**", "OPTIONS")).permitAll()
                                .requestMatchers(new AntPathRequestMatcher("/users/**", "GET")).permitAll()
                                .requestMatchers(new AntPathRequestMatcher("/users/**", "PATCH")).permitAll()
                                .requestMatchers(new AntPathRequestMatcher("/voc/list", "GET")).permitAll()
                                .requestMatchers(new AntPathRequestMatcher("/voc/count-by-category", "GET")).permitAll()
                                .requestMatchers(new AntPathRequestMatcher("/voc/filter", "POST")).permitAll()
                                .requestMatchers(new AntPathRequestMatcher("/voc/count-by-category", "GET")).permitAll()
                                .requestMatchers(new AntPathRequestMatcher("/member/**", "GET")).permitAll()
                                .requestMatchers(new AntPathRequestMatcher("/campaign/**", "GET")).permitAll()
                                .requestMatchers(new AntPathRequestMatcher("/campaign/**", "POST")).permitAll()
                                .requestMatchers(new AntPathRequestMatcher("/campaign/**", "PATCH")).permitAll()
                                .requestMatchers(new AntPathRequestMatcher("/campaign/**", "DELETE")).permitAll()
                                .requestMatchers(new AntPathRequestMatcher("/campaign-template/**", "GET")).permitAll()
                                .requestMatchers(new AntPathRequestMatcher("/campaign-template/**", "PATCH")).permitAll()
                                .requestMatchers(new AntPathRequestMatcher("/campaign-template/**", "POST")).permitAll()
                                .requestMatchers(new AntPathRequestMatcher("/coupon/**", "GET")).permitAll()
                                .requestMatchers(new AntPathRequestMatcher("/coupon/**", "POST")).permitAll()
                                .requestMatchers(new AntPathRequestMatcher("/coupon/**", "PATCH")).permitAll()
                                .requestMatchers(new AntPathRequestMatcher("/member/**", "POST")).permitAll()
                                .requestMatchers(new AntPathRequestMatcher("/member/excel/**", "POST")).permitAll()
                                .requestMatchers(new AntPathRequestMatcher("/issue-coupon/**", "GET")).permitAll()
                                .requestMatchers(new AntPathRequestMatcher("/issue-coupon/**", "POST")).permitAll()
                                .requestMatchers(new AntPathRequestMatcher("/voc-answer/register", "POST")).permitAll()
                                .requestMatchers(new AntPathRequestMatcher("/voc-answer/edit/**", "PATCH")).permitAll()
                                .requestMatchers(new AntPathRequestMatcher("/lecture/**", "PATCH")).permitAll()
                                .requestMatchers(new AntPathRequestMatcher("/lecture/**", "POST")).permitAll()
                                .requestMatchers(new AntPathRequestMatcher("/lecture/**", "GET")).permitAll()
                                .requestMatchers(new AntPathRequestMatcher("/lecture/excel/**", "POST")).permitAll()
                                .requestMatchers(new AntPathRequestMatcher("/blacklist/**", "GET")).permitAll()
                                .requestMatchers(new AntPathRequestMatcher("/blacklist/**", "POST")).permitAll()
                                .requestMatchers(new AntPathRequestMatcher("/blacklist/**", "PATCH")).permitAll()
                                .requestMatchers(new AntPathRequestMatcher("/client/**", "GET")).permitAll()
                                .requestMatchers(new AntPathRequestMatcher("/client/**", "POST")).permitAll()
                                .requestMatchers(new AntPathRequestMatcher("/voc/ai/**", "GET")).permitAll()
                                .anyRequest().authenticated()
                )
                // UserDetails를 상속받는 Service 계층 + BCrypt 암호화
                .authenticationManager(authenticationManager)
                // 서버가 세션을 생성하지 않음
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));


        // JWT 인증 필터 추가
        http.addFilter(getAuthenticationFilter(authenticationManager));
        http.addFilterBefore(new JwtFilter(userService, jwtUtil), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Collections.singletonList("http://localhost:5173")); // 프론트엔드 도메인 허용
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH")); // 허용할 HTTP 메서드 설정
        configuration.setAllowCredentials(true); // 인증 정보 허용 (쿠키 등)
        configuration.setAllowedHeaders(Collections.singletonList("*")); // 모든 헤더 허용
        configuration.setMaxAge(3600L); // 1시간 동안 캐시

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    // Authentication 용 메소드(인증 필터 반환)
    private Filter getAuthenticationFilter(AuthenticationManager authenticationManager) {
        return new AuthenticationFilter(authenticationManager, userService, env, jwtUtil, redisTemplate);
    }

}
