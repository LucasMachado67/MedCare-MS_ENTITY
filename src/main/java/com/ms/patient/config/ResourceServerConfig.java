package com.ms.patient.config;

import java.util.List;

import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;

import io.jsonwebtoken.io.Decoders;

/**
 * Configuração de segurança do Resource Server para o microsserviço de gerenciamento de entidades.
 * * <p>Esta classe define como as requisições HTTP devem ser tratadas, estabelecendo
 * permissões de acesso, desabilitando proteções desnecessárias para APIs stateless (como CSRF)
 * e configurando a validação e decodificação de tokens JWT.</p>
 * 
 * @version 1.0
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class ResourceServerConfig {

    @Value("${api.security.token.secret}")
    private String secret;
    /**
     * Define a corrente de filtros de segurança (Security Filter Chain).
     * * Configura:
     * <ul>
     * <li>Desabilitação do CSRF.</li>
     * <li>Liberação pública dos endpoints do Swagger/OpenAPI.</li>
     * <li>Liberação do endpoint de criação de pacientes (auto-cadastro).</li>
     * <li>Exigência de autenticação para qualquer outra rota.</li>
     * <li>Configuração do suporte a JWT como padrão do Resource Server.</li>
     * </ul>
     * * @param http Objeto HttpSecurity para configurar a segurança baseada em web.
     * @return SecurityFilterChain configurado.
     * @throws Exception Caso ocorra erro na configuração.
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
        .csrf(csrf -> csrf.disable()) 
        .authorizeHttpRequests(auth -> auth
            // Liberando Swagger
            .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()
            .requestMatchers("/patient/create").permitAll()
            .anyRequest().authenticated()
        )
        .oauth2ResourceServer(oauth2 -> oauth2
                .jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter()))
        );

        return http.build();
    }
    /**
     * Configura o decodificador de JWT utilizando uma chave secreta simétrica.
     * * <p>O método decodifica a chave Base64 fornecida nas propriedades do sistema
     * e utiliza o algoritmo HMAC-SHA256 para validar a assinatura dos tokens recebidos.</p>
     * * @return Um bean de {@link JwtDecoder} configurado com a chave secreta.
     */
    @Bean
    public JwtDecoder jwtDecoder() {
        byte[] keyBytes = Decoders.BASE64.decode(secret);
    
        SecretKeySpec secretKey = new SecretKeySpec(keyBytes, "HmacSHA256");
        
        return NimbusJwtDecoder.withSecretKey(secretKey).build();
    }
    /**
     * Personaliza a conversão do JWT para o contexto de segurança do Spring.
     * * <p>Este conversor extrai a claim "role" do payload do JWT e a transforma em uma 
     * autoridade do Spring Security, prefixando-a com "ROLE_". Isso permite o uso 
     * de anotações como {@code @PreAuthorize("hasRole('ADMIN')")}.</p>
     * * @return Um {@link JwtAuthenticationConverter} com lógica de mapeamento de roles.
     */
    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {

        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();

        converter.setJwtGrantedAuthoritiesConverter(jwt -> {
            String role = jwt.getClaim("role");

            return List.of(new SimpleGrantedAuthority("ROLE_" + role));
        });

        return converter;
    }
}
