package fr.codecake.airbnbclone.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;

import java.util.HashSet;
import java.util.Set;

@Configuration
@EnableMethodSecurity
public class SecurityConfiguration {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(authorize -> authorize
                .requestMatchers(HttpMethod.GET, "/api/tenant-listing/get-all-by-category").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/tenant-listing/get-one").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/tenant-listing/search").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/booking/check-availability").permitAll()
                .requestMatchers(HttpMethod.GET, "/assets/*").permitAll()
                .anyRequest().authenticated())
            .csrf(csrf -> csrf
                .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse()));
    
        return http.build();
    }
    

    @Bean
    public GrantedAuthoritiesMapper userAuthoritiesMapper() {
        return authorities -> {
            Set<GrantedAuthority> grantedAuthorities = new HashSet<>();
            authorities.forEach(grantedAuthority -> {
                grantedAuthorities.add(new SimpleGrantedAuthority(grantedAuthority.getAuthority()));
            });
            return grantedAuthorities;
        };
    }
    
}
