package main.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import main.dao.IRolDAO;
import main.dto.Rol;

@Service
public class RolService {

	@Autowired
	IRolDAO iRolDAO;
	
	public Rol buscarRol(String name) {
		return iRolDAO.findByName(name);
	}
	
	public Rol guardarRol(Rol rol) {
		return iRolDAO.save(rol);
	}
	
	public Rol buscarRolXID(Long id) {
		return iRolDAO.findById(id).get();
	}
}
