package main.services;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import main.dao.IUsuarioDAO;
import main.dto.Rol;
import main.dto.Usuario;
import main.dto.UsuarioRol;

@Service
public class UsuarioService implements IUsuarioService, UserDetailsService {

	@Autowired
	IUsuarioDAO iUsuarioDAO;
	@Autowired
	UsuarioRolService usuarioRolService;
		
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		Usuario usuario = iUsuarioDAO.findByUsername(username);
		if (usuario == null) {
			throw new UsernameNotFoundException(username);
		}
		//Creo una colección que contrendrá los roles del usuario
		Collection<SimpleGrantedAuthority> autoridades = new ArrayList<>();
		
		//Busco todos los roles que posee el usuario
		List<UsuarioRol> rolesUsuario = usuarioRolService.buscarRolesUsuario(usuario);
		rolesUsuario.forEach(rolUsuario -> {
			// Retorno el rol
			Rol rol = rolUsuario.getRol();
			// Retorno el nombre del rol del usuario y lo agrego a la lista de "autoridades"
			autoridades.add(new SimpleGrantedAuthority("ROLE_" + rol.getName()));
		});
		return new User(usuario.getUsername(), usuario.getPassword(), autoridades);
	}
	
	@Override
	public List<Usuario> listarUsuarios() {
		
		return iUsuarioDAO.findAll();
	}

	@Override
	public Usuario buscarUsuario(String username) {
		
		return iUsuarioDAO.findByUsername(username);
	}

	@Override
	public Usuario guardarUsuario(Usuario usuario) {

		return iUsuarioDAO.save(usuario);
	}

}
