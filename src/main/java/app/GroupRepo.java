package app;

import java.util.List;
import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

public interface GroupRepo extends CrudRepository<Group, Integer> {
	
	public List<Group> findAll();
	
	public Optional<Group> findById(Integer id);
	
}
