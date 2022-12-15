package main.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import main.dao.IUsuarioRolDAO;
import main.dto.Usuario;
import main.dto.UsuarioRol;

@Service
public class UsuarioRolService {

	
	@Autowired
	IUsuarioRolDAO iUsuarioRolDAO;
	
	public UsuarioRol guardaUsuarioRol(UsuarioRol usuarioRol) {
		return iUsuarioRolDAO.save(usuarioRol);
	}
	
	public List<UsuarioRol> listaUsuarioRols (){
		return iUsuarioRolDAO.findAll();
	}
	
	public List<UsuarioRol> buscarRolesUsuario(Usuario usuario){
		return iUsuarioRolDAO.findByUsuario(usuario);
	}
}
