package pe.edu.upeu.autoandes.config;
import org.springframework.context.annotation.*; import org.springframework.http.HttpMethod; import org.springframework.security.config.annotation.web.builders.HttpSecurity; import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity; import org.springframework.security.config.http.SessionCreationPolicy; import org.springframework.security.web.*; import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter; import org.springframework.security.core.userdetails.UserDetailsService; import org.springframework.security.crypto.password.PasswordEncoder; import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder; import pe.edu.upeu.autoandes.security.*;
@Configuration @EnableMethodSecurity public class SecurityConfig {
@Bean PasswordEncoder passwordEncoder(){return new BCryptPasswordEncoder();}
@Bean SecurityFilterChain security(HttpSecurity http,JwtService jwt,UserDetailsService users,RestAuthenticationEntryPoint entrada,RestAccessDeniedHandler denegado)throws Exception {
return http.csrf(c->c.disable()).cors(c->{}).logout(c->c.disable()).sessionManagement(s->s.sessionCreationPolicy(SessionCreationPolicy.STATELESS)).authorizeHttpRequests(a->a
.requestMatchers(HttpMethod.OPTIONS,"/**").permitAll().requestMatchers("/api/v1/auth/login","/swagger-ui.html","/swagger-ui/**","/v3/api-docs/**").permitAll()
.requestMatchers(HttpMethod.GET,"/api/v1/servicios","/api/v1/servicios/**").hasAuthority("SERVICIO_LEER")
.requestMatchers("/api/v1/servicios","/api/v1/servicios/**").hasAuthority("SERVICIO_ESCRIBIR")
.requestMatchers(HttpMethod.GET,"/api/v1/clientes","/api/v1/vehiculos","/api/v1/vehiculos/**").hasAuthority("VEHICULO_LEER")
.requestMatchers("/api/v1/vehiculos","/api/v1/vehiculos/**").hasAuthority("VEHICULO_ESCRIBIR")
.requestMatchers(HttpMethod.POST,"/api/v1/ordenes").hasAuthority("ORDEN_REGISTRAR")
.requestMatchers(HttpMethod.PATCH,"/api/v1/ordenes/*/anular").hasAuthority("ORDEN_ANULAR")
.requestMatchers(HttpMethod.GET,"/api/v1/ordenes","/api/v1/ordenes/**").hasAuthority("ORDEN_LEER")
.requestMatchers("/api/v1/reportes/**").hasAuthority("REPORTE_VER").anyRequest().authenticated())
.exceptionHandling(e->e.authenticationEntryPoint(entrada).accessDeniedHandler(denegado))
.addFilterBefore(new JwtAuthenticationFilter(jwt,users),UsernamePasswordAuthenticationFilter.class).build();}}
