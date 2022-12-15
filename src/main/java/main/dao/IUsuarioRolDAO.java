package main.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import main.dto.Usuario;
import main.dto.UsuarioRol;

public interface IUsuarioRolDAO extends JpaRepository<UsuarioRol, Long>{

	public List<UsuarioRol> findByUsuario(Usuario usuario);
	
}
