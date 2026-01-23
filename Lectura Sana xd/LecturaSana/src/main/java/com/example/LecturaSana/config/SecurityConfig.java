package com.example.LecturaSana.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import com.example.LecturaSana.repository.UsuarioRepository;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UserDetailsService userDetailsService(UsuarioRepository usuarioRepository) {
        return (email) -> usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + email));
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // Desactivamos CSRF para facilitar el desarrollo (esto no afecta SQL Injection)
            .csrf(csrf -> csrf.disable()) 
            
            // --- AQUÍ QUITAMOS TODO EL BLOQUE ".headers(...)" ---
            // Al quitarlo, el navegador ya no bloqueará tus estilos ni scripts.

            .authorizeHttpRequests(auth -> auth
                // 1. RECURSOS ESTÁTICOS (Imágenes, CSS, JS)
                .requestMatchers("/css/**", "/js/**", "/IMG/**", "/images/**").permitAll()
                
                // 2. RUTAS PÚBLICAS (Login, Registro, Catálogo, API del carrito)
                .requestMatchers(
                    "/", 
                    "/auth/**",           // Login
                    "/registro", 
                    "/procesar_registro",
                    "/catalogo", 
                    "/novedades",      
                    "/detalle/**",        // Ver noticia
                    "/libro/**",          // Ver libro detalle
                    "/api/carrito/**",    // Para que el contador del carrito funcione
                    "/error/**"           // Páginas de error
                ).permitAll()

                // 3. RUTAS DE ADMIN (PUBLICADOR)
                .requestMatchers("/libros/**", "/admin/**").hasRole("PUBLICADOR")

                // 4. RUTAS DE USUARIO (Cualquier otra cosa requiere login)
                .anyRequest().authenticated() 
            )
            
            .formLogin(form -> form
                .loginPage("/auth/login")
                .loginProcessingUrl("/auth/login-process")
                .defaultSuccessUrl("/", true)
                .failureUrl("/auth/login?error=true")
                .permitAll()
            )
            
            .logout(logout -> logout
                .logoutUrl("/auth/logout")
                .logoutSuccessUrl("/auth/login?logout=true")
                .permitAll()
            );
            

        return http.build();
    }
}