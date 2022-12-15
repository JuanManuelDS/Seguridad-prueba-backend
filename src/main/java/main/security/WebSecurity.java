package main.security;

import static main.security.Constants.LOGIN_URL;
import static main.security.Constants.REGISTER_URL;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
public class WebSecurity extends WebSecurityConfigurerAdapter {

	private UserDetailsService userDetailsService;

	public WebSecurity(UserDetailsService userDetailsService) {
		this.userDetailsService = userDetailsService;
	}

	@Bean
	public BCryptPasswordEncoder bCryptPasswordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Override
	protected void configure(HttpSecurity httpSecurity) throws Exception {
		httpSecurity
			.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and() // Se desactiva el uso de cookies
			.cors().and() // Se activa la configuración CORS con los valores por defecto
			.csrf().disable() //Se desactiva el filtro CSRF
			.authorizeRequests().antMatchers(HttpMethod.POST, LOGIN_URL).permitAll();// Se indica que el /login no requiere autenticación
		
		httpSecurity.authorizeRequests().antMatchers(HttpMethod.POST, REGISTER_URL).permitAll();//La página register tampoco
		
		httpSecurity.authorizeRequests().antMatchers(HttpMethod.POST, "/api/usuarios/roles").permitAll(); //Tampoco para agregar roles a usuarios
		
		httpSecurity.authorizeRequests().antMatchers(HttpMethod.GET, "/api/usuarios").hasRole("ADMIN") //Le digo que solo los que tengan rol de admin podrán ver todos los usuarios	
		.anyRequest().authenticated().and() //Se indica que el resto de URLs sí requieran autentificación
				.addFilter(new JWTAuthenticationFilter(authenticationManager()))
				.addFilter(new JWTAuthorizationFilter(authenticationManager()));
	}

	@Override
	public void configure(AuthenticationManagerBuilder auth) throws Exception {
		
		// Se define la clase que recupera los usuarios y el algoritmo para procesar las passwords
		auth.userDetailsService(userDetailsService).passwordEncoder(bCryptPasswordEncoder());
	}

	@Bean
	CorsConfigurationSource corsConfigurationSource() {
		final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", new CorsConfiguration().applyPermitDefaultValues());
		return source;
	}
}