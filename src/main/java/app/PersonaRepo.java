package app;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

public interface PersonaRepo extends CrudRepository<Persona, Integer> {
	
	@Query("from Persona p where lower(lastName) = lower(?1)")
	public List<Persona> findByLastName(String lastName);
	
}
