package main.security;

import static main.security.Constants.HEADER_AUTHORIZACION_KEY;
import static main.security.Constants.SUPER_SECRET_KEY;
import static main.security.Constants.TOKEN_BEARER_PREFIX;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import main.services.UsuarioService;

public class JWTAuthorizationFilter extends BasicAuthenticationFilter {

	public JWTAuthorizationFilter(AuthenticationManager authManager) {
		super(authManager);
	}

	@Override
	protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain)
			throws IOException, ServletException {
		
		//retorno el header del request, authorization key para ser exactos
		String header = req.getHeader(HEADER_AUTHORIZACION_KEY);
		
		//En caso que el cliente no tenga un token o este no comience con el token bearer(no es correcto), retorna null
		if (header == null || !header.startsWith(TOKEN_BEARER_PREFIX)) {
			chain.doFilter(req, res);
		
		} else {
			//En caso contrario, se retorna la autentificación de la request(req) 	
			UsernamePasswordAuthenticationToken authentication = getAuthentication(req);
			SecurityContextHolder.getContext().setAuthentication(authentication);
			chain.doFilter(req, res);
		}
		
	}

	private UsernamePasswordAuthenticationToken getAuthentication(HttpServletRequest request) {
		
		//Tomo el token con la clave "authorization" definida en la clase Constants
		String token = request.getHeader(HEADER_AUTHORIZACION_KEY);
		if (token != null) {
			// Se procesa el token y se recupera el usuario.
			Claims claims = Jwts.parser()
						.setSigningKey(SUPER_SECRET_KEY)
						.parseClaimsJws(token.replace(TOKEN_BEARER_PREFIX, "")) //Aquí le saco el token bearer prefix
						.getBody();
			//Parseo el string a una colección de GrantedAuthority
			final Collection<? extends GrantedAuthority> authorities =
	                Arrays.stream(claims.get("roles").toString().split(","))
	                        .map(SimpleGrantedAuthority::new)
	                        .collect(Collectors.toList());
			if (claims != null) {
				return new UsernamePasswordAuthenticationToken(claims.getSubject(), null, authorities);
			}
			return null;
		}
		return null;
	}
}