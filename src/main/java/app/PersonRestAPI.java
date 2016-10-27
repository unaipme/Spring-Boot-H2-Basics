package app;

import java.net.URI;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@RestController
@RequestMapping("/people")
public class PersonRestAPI {
	
	private final Logger log = LogManager.getLogger(PersonRestAPI.class.getName());
	
	@Autowired
	private PersonRepo repo;
	
	@GetMapping
	@ResponseStatus(HttpStatus.OK)
	@ApiOperation(value="Returns all the people stored in the database.")
	@ApiResponse(code=200, message = "OK")
	public List<Person> getAll() {
		return repo.findAll();
	}
	
	@GetMapping("/{id}")
	@ApiOperation(value="Fetches one person from the database using its ID.")
	@ApiResponses(value={
			@ApiResponse(code=200, message="OK"),
			@ApiResponse(code=404, message="No person has been found with the given ID.")
	})
	public ResponseEntity<Person> getOne(@PathVariable Integer id) {
		Optional<Person> p = repo.findById(id);
		if (p.isPresent()) {
			return new ResponseEntity<>(p.get(), HttpStatus.OK);
		} else {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
	}
	
	@GetMapping("/{id}/group")
	@ResponseStatus(HttpStatus.OK)
	@ApiOperation(value="Gets the group (if any) of the person with the given ID.")
	@ApiResponses(value = {
			@ApiResponse(code=200, message="OK"),
			@ApiResponse(code=404, message="Either no person with the given ID has been found or the person does not belong to any group.")
	})
	public ResponseEntity<Group> getGroup(@PathVariable Integer id) {
		Optional<Person> o = repo.findById(id);
		if (!o.isPresent()) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		} else {
			Person p = o.get();
			if (p.getGroup() == null) {
				return new ResponseEntity<>(HttpStatus.NOT_FOUND);
			} else {
				HttpHeaders h = new HttpHeaders();
				h.setLocation(URI.create(String.format("/groups/%d", p.getGroup().getId())));
				return new ResponseEntity<>(p.getGroup(), h, HttpStatus.OK);
			}
		}
	}
	
	@PostMapping(params = {"firstName", "lastName", "birthday"})
	@ApiOperation(value="Creates a new person in the database.")
	@ApiResponses(value = {
			@ApiResponse(code=200, message="OK"),
			@ApiResponse(code=201, message="The person has been successfully created."),
			@ApiResponse(code=500, message="An error occurred in the server.")
	})
	public ResponseEntity<Person> create(@RequestParam(value="firstName") String firstName,
						@RequestParam(value="lastName") String lastName,
						@RequestParam(value="birthday") String birthday) {
		try {
			Person p = new Person(firstName, lastName, LocalDate.parse(birthday));
			repo.save(p);
			HttpHeaders h = new HttpHeaders();
			h.setLocation(URI.create(String.format("/people/%d", p.getId())));
			return new ResponseEntity<>(p, h, HttpStatus.CREATED);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@PutMapping("/{id}")
	@ApiOperation(value="Updates a person's information.", notes="Can take first name, last name and birthday and update the value of the person with the given ID.")
	@ApiResponses(value={
			@ApiResponse(code=204, message="The information was updated successfully."),
			@ApiResponse(code=404, message="No person could be found with the given ID."),
			@ApiResponse(code=500, message="An error occurred in the server.")
	})
	public ResponseEntity<Person> update(@PathVariable Integer id,
						@RequestParam(value="firstName", required=false) String firstName,
						@RequestParam(value="lastName", required=false) String lastName,
						@RequestParam(value="birthday", required=false) String birthday) {
		try {	
			Optional<Person> o = repo.findById(id);
			if (!o.isPresent()) {
				return new ResponseEntity<>(HttpStatus.NOT_FOUND);
			} else {
				Person p = o.get();
				if (firstName != null) {
					p.setFirstName(firstName);
				}
				if (lastName != null) {
					p.setLastName(lastName);
				}
				if (birthday != null) {
					p.setBirthday(LocalDate.parse(birthday));
				}
				repo.save(p);
				return new ResponseEntity<>(p, HttpStatus.NO_CONTENT);
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@DeleteMapping(params={"id"})
	@ApiOperation(value="Deletes the person with the given ID.")
	@ApiResponses(value={
			@ApiResponse(code=204, message="The person was successfully deleted."),
			@ApiResponse(code=404, message="The person with the given ID was not found."),
			@ApiResponse(code=500, message="An error occurred in the server.")
	})
	public ResponseEntity<Void> delete(@RequestParam(value="id") Integer id) {
		try {
			Optional<Person> op = repo.findById(id);
			if (op.isPresent()) {
				repo.delete(id);
				return new ResponseEntity<>(HttpStatus.NO_CONTENT);
			} else {
				return new ResponseEntity<>(HttpStatus.NOT_FOUND);
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
}
