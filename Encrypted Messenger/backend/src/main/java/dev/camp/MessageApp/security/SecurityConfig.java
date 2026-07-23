package dev.camp.MessageApp.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {
    @Value("${dev.camp.security.environment:prod}")
    private String env;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) {
        httpSecurity
                .cors(Customizer.withDefaults())
                .authorizeHttpRequests((requests) -> {
                        requests.requestMatchers(HttpMethod.POST, "/users").permitAll()
                            .requestMatchers(HttpMethod.PATCH, "/users/**").permitAll()
                            .requestMatchers(HttpMethod.GET, "/users").hasAuthority(Roles.USER)
                            .requestMatchers(HttpMethod.POST, "/securemsg").hasAuthority(Roles.USER)
                            .requestMatchers(HttpMethod.GET, "/securemsg").hasAuthority(Roles.USER);

                        if (env.equals("dev")) {
                            requests.anyRequest().permitAll();
                        }
                        else {
                            requests.anyRequest().denyAll();
                        }

                }).httpBasic(Customizer.withDefaults());

        if (env.equals("dev")) {
            httpSecurity
                    .csrf(AbstractHttpConfigurer::disable)
                    .headers(AbstractHttpConfigurer::disable);
        } else {
            httpSecurity
                    .headers((headers) ->
                            headers.contentSecurityPolicy((csp) -> csp
                                    .policyDirectives("script-src 'self'"))
                    );
        }
        return httpSecurity.build();
    }
}
