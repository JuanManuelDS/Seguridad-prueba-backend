package main.services;

import java.util.List;

import main.dto.Usuario;

public interface IUsuarioService {
	 
	public List<Usuario> listarUsuarios();
	
	public Usuario buscarUsuario(String username);
	
	public Usuario guardarUsuario(Usuario usuario);
	
}
