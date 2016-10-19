package app;

import java.time.LocalDate;
import java.util.List;

import org.apache.commons.collections.IteratorUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/people")
public class PersonaRest {
	
	private final Logger log = LogManager.getLogger(PersonaRest.class.getName());
	
	@Autowired
	private PersonaRepo repo;
	
	@SuppressWarnings("unchecked")
	@GetMapping
	public List<Persona> getAll() {
		return IteratorUtils.toList(repo.findAll().iterator());
	}
	
	@GetMapping(params={"lastName"})
	public List<Persona> getByLastName(@RequestParam(value="lastName") String lastName) {
		return repo.findByLastName(lastName);
	}
	
	@GetMapping("/{id}")
	public Persona getOne(@PathVariable Integer id) {
		return repo.findOne(id);
	}
	
	@GetMapping("/{id}/group")
	public Grupo getGroup(@PathVariable Integer id) {
		return repo.findOne(id).getGroup();
	}
	
	@PostMapping(params = {"firstName", "lastName", "birthday"})
	public String create(@RequestParam(value="firstName") String firstName,
						@RequestParam(value="lastName") String lastName,
						@RequestParam(value="birthday") String birthday) {
		try {
			Persona p = new Persona(firstName, lastName, LocalDate.parse(birthday));
			repo.save(p);
			return "Everything OK!";
		} catch (Exception e) {
			log.error(e.getMessage());
			return e.getMessage();
		}
	}
	
	@PutMapping("/{id}")
	public String update(@PathVariable Integer id,
						@RequestParam(value="firstName", required=false) String firstName,
						@RequestParam(value="lastName", required=false) String lastName,
						@RequestParam(value="birthday", required=false) String birthday) {
		try {	
			Persona p = repo.findOne(id);
			if (firstName != null) p.setFirstName(firstName);
			if (lastName != null) p.setLastName(lastName);
			if (birthday != null) p.setBirthday(LocalDate.parse(birthday));
			repo.save(p);
			return "Everything OK!";
		} catch (Exception e) {
			return e.getMessage();
		}
	}
	
	@DeleteMapping(params={"id"})
	public String delete(@RequestParam(value="id") Integer id) {
		try {
			repo.delete(id);
			return "Everything OK!";
		} catch (Exception e) {
			return e.getMessage();
		}
	}
	
}
