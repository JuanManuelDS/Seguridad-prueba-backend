package main.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import main.dto.Rol;

public interface IRolDAO extends JpaRepository<Rol, Long> {

	public Rol findByName(String name);
	
}
