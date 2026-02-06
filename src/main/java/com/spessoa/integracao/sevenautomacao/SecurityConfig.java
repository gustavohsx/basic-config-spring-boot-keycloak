package com.spessoa.integracao.sevenautomacao;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity // Habilita segurança em nível de método (ex: @PreAuthorize)
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable) // Desabilita CSRF para APIs REST
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/public/**").permitAll() // Exemplo: permite acesso a endpoints públicos
                        .anyRequest().authenticated() // Todas as outras requisições requerem autenticação
                )
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> jwt.jwtAuthenticationConverter(new CustomJwtAuthenticationConverter())) // Opcional: Adicione um conversor de JWT para mapear roles
                )
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)); // Não cria sessão, pois a autenticação é stateless (via JWT)
        return http.build();
    }

    // Configuração CORS Bean
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        // Permite o seu domínio Angular. MUITO IMPORTANTE!
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:4200")); // Adicione a URL do seu frontend Angular
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS")); // Métodos HTTP permitidos
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type", "Accept")); // Cabeçalhos permitidos
        configuration.setAllowCredentials(true); // Permite credenciais (cookies, headers de autorização)
        configuration.setExposedHeaders(Arrays.asList("Link", "X-Total-Count")); // Opcional: Se sua API expõe outros cabeçalhos

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration); // Aplica a configuração para todas as rotas
        return source;
    }
}