package main.controllers;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import main.dto.Rol;
import main.dto.Usuario;
import main.dto.UsuarioRol;
import main.services.RolService;
import main.services.UsuarioRolService;
import main.services.UsuarioService;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*", methods= {RequestMethod.GET,RequestMethod.POST,RequestMethod.PUT,RequestMethod.DELETE})
public class UsuarioController {

	@Autowired
	UsuarioService usuarioService;
	
	@Autowired
	RolService rolService;
	
	@Autowired
	UsuarioRolService usuarioRolService;
	
	private final BCryptPasswordEncoder bCryptPasswordEncoder;

	public UsuarioController(BCryptPasswordEncoder bCryptPasswordEncoder) {
		this.bCryptPasswordEncoder = bCryptPasswordEncoder;
	}
	
	@GetMapping("/response-entity-builder-with-http-headers")
	public ResponseEntity<String> usingResponseEntityBuilderAndHttpHeaders() {
	    HttpHeaders responseHeaders = new HttpHeaders();
	    responseHeaders.set("Baeldung-Example-Header", 
	      "Value-ResponseEntityBuilderWithHttpHeaders");

	    return ResponseEntity.ok()
	      .headers(responseHeaders)
	      .body("Response with header using ResponseEntity");
	}
	
	//Retorna la lista de roles de un usuario seleccionado
	@GetMapping("/usuarios/roles/{username}")
	public List<Rol> listarRolesUsuario(@PathVariable(name = "username") String username) {

		Usuario usuario = usuarioService.buscarUsuario(username);

		List<UsuarioRol> rolesUsuario = usuarioRolService.buscarRolesUsuario(usuario);
		List<Rol> roles = new ArrayList<>();
		rolesUsuario.forEach(rolUsuario -> {
			roles.add(rolUsuario.getRol());
		});
		return roles;

	}
	@GetMapping("/usuarios")
	public List<Usuario> listasrUsuarios(){
		return usuarioService.listarUsuarios();
	}
	
	@GetMapping("/usuarios/buscar/{username}")
	public Usuario buscarUsuario(@PathVariable(name="username")String username) {
		return usuarioService.buscarUsuario(username);
	}
	
	@PostMapping("/usuarios")
	public Usuario guardarUsuario(@RequestBody Usuario usuario) {
		usuario.setPassword(bCryptPasswordEncoder.encode(usuario.getPassword()));
		return usuarioService.guardarUsuario(usuario);
	}
	
	/*Uso este método para validar el token desde el front y retornar la información del usuario (nombre de usuario y roles)*/
	@GetMapping("/usuarios/verificar")
	public UsuarioInfo getUsuario() {
		String username = SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString();
		String credenciales = SecurityContextHolder.getContext().getAuthentication().getAuthorities().toString();
		
		
		return new UsuarioInfo(username, credenciales);
	}
	
	//Agrega un rol a un usuario
	@PostMapping("/usuarios/roles")
	public UsuarioRol agregarRolUsuario(@RequestBody RolAUsuarioForm form) {
		System.out.println(form.getUsername());
		Usuario usuario = usuarioService.buscarUsuario(form.getUsername());
		Rol rol = rolService.buscarRol(form.getRoleName());
		UsuarioRol usuarioRol = new UsuarioRol(null,usuario, rol);
		return usuarioRolService.guardaUsuarioRol(usuarioRol);
	}
	
}

//Uso esta clase para retornar la información del usuario cuando valido el token desde el front
class UsuarioInfo {
	private String username;
	private String roles;
	
	public UsuarioInfo(String username, String roles) {
		this.username = username;
		this.roles = roles;
	}
	
	public String getUsername() {
		return username;
	}
	
	public String getRoles() {
		return roles;
	}
}


//Utilizo esta clase para el método agregarRolUsuario, para dar forma de objeto a la request
class RolAUsuarioForm {
	private String username;
	private String roleName;

	public RolAUsuarioForm(String username, String roleName) {
		this.username = username;
		this.roleName = roleName;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getRoleName() {
		return roleName;
	}

	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}

}
