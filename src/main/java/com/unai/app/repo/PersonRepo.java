package com.unai.app.repo;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.unai.app.model.Person;

public interface PersonRepo extends CrudRepository<Person, Integer> {
	
	@Query("from Person p where lower(lastName) = lower(?1)")
	public List<Person> findByLastName(String lastName);
	
	public Optional<Person> findById(Integer id);
	
	public List<Person> findAll();
	
}
