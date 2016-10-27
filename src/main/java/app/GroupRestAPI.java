package app;

import java.net.URI;
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
@RequestMapping("/groups")
public class GroupRestAPI {
	
	private final Logger log = LogManager.getLogger(GroupRestAPI.class.getName());
	
	@Autowired
	private GroupRepo gRepo;
	
	@Autowired
	private PersonRepo pRepo;
	
	@GetMapping
	@ResponseStatus(HttpStatus.OK)
	@ApiOperation(value="Gets all the groups currently stored in the database.")
	@ApiResponse(code=200, message="OK")
	public List<Group> getAll() {
		return gRepo.findAll();
	}
	
	@GetMapping(value="/{id}")
	@ApiOperation(value="Gets one group's information given the ID.")
	@ApiResponses(value = {
			@ApiResponse(code = 200, message="OK"),
			@ApiResponse(code = 404, message="No group was found with the given ID.")
	})
	public ResponseEntity<Group> getOne(@PathVariable Integer id) {
		Optional<Group> o = gRepo.findById(id);
		if (o.isPresent()) {
			return new ResponseEntity<>(o.get(), HttpStatus.OK);
		} else {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
	}
	
	@GetMapping(value="/{id}/members")
	@ApiOperation(value="Returns a list with the members of the group with the given ID.")
	@ApiResponses(value={
			@ApiResponse(code=200, message="OK"),
			@ApiResponse(code=404, message="No group was found with the given ID.")
	})
	public ResponseEntity<List<Person>> getMembers(@PathVariable Integer id) {
		Optional<Group> o = gRepo.findById(id);
		if (o.isPresent()) {
			return new ResponseEntity<>(o.get().getMembers(), HttpStatus.OK);
		} else {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
	}
	
	@PutMapping(params={"pid"})
	@ApiOperation(value="Adds a member to a group, receiving the IDs of the group and the person")
	@ApiResponses(value={
			@ApiResponse(code=204, message="The person's membership was updated successfully."),
			@ApiResponse(code=404, message="Either the person or the group could not be found with the given ID."),
			@ApiResponse(code=500, message="An error occurred in the server")
	})
	public ResponseEntity<Group> addMember(@RequestParam(value="gid", required=false) Integer gid,
							@RequestParam(value="pid") Integer pid) {
		try {
			Optional<Person> op = pRepo.findById(pid);
			if (op.isPresent()) {
				Person p = op.get();
				if (gid == null) {
					p.setGroup(null);
					pRepo.save(p);
					return new ResponseEntity<>(HttpStatus.NO_CONTENT);
				}
				Optional<Group> og = gRepo.findById(gid);
				if (og.isPresent()) {
					Group g = og.get();
					p.setGroup(g);
					g.getMembers().add(p);
					pRepo.save(p);
					gRepo.save(g);
					HttpHeaders h = new HttpHeaders();
					h.setLocation(URI.create(String.format("/groups/%d/members", g.getId())));
					return new ResponseEntity<>(HttpStatus.NO_CONTENT);
				} else {
					return new ResponseEntity<>(HttpStatus.NOT_FOUND);
				}
			} else {
				return new ResponseEntity<>(HttpStatus.NOT_FOUND);
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@PutMapping("/{id}")
	@ApiOperation(value="Updates the information of the group with the given ID")
	@ApiResponses(value={
			@ApiResponse(code=204, message="The group was updated successfully."),
			@ApiResponse(code=404, message="The group with the given ID could not be found."),
			@ApiResponse(code=500, message="An error occurred in the server.")
	})
	public ResponseEntity<Void> update(@RequestParam(value="name", required=false) String name,
						@PathVariable Integer id) {
		try {
			Optional<Group> og = gRepo.findById(id);
			if (og.isPresent()) {
				Group g = og.get();
				if (name != null) {
					g.setName(name);
				}
				gRepo.save(g);
				HttpHeaders h = new HttpHeaders();
				h.setLocation(URI.create(String.format("/groups/%d", g.getId())));
				return new ResponseEntity<>(h, HttpStatus.NO_CONTENT);
			} else {
				return new ResponseEntity<>(HttpStatus.NOT_FOUND);
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@PostMapping(params={"name"})
	@ApiOperation(value="Creates a new group")
	@ApiResponses(value={
			@ApiResponse(code=204, message="The group was created successfully."),
			@ApiResponse(code=500, message="An error occurred in the server.")
	})
	public ResponseEntity<Group> create(@RequestParam(value="name") String name) {
		try {
			Group g = new Group(name);
			gRepo.save(g);
			HttpHeaders h = new HttpHeaders();
			h.setLocation(URI.create(String.format("/groups/%d", g.getId())));
			return new ResponseEntity<>(g, h, HttpStatus.CREATED);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@DeleteMapping(params={"id"})
	@ApiOperation(value="Deletes the group with the given ID")
	@ApiResponses(value={
			@ApiResponse(code=204, message="The group was deleted succesfully."),
			@ApiResponse(code=404, message="The group with the given ID could not be found."),
			@ApiResponse(code=500, message="An error occurred in the server")
	})
	public ResponseEntity<Void> delete(@RequestParam(value="id") Integer id) {
		try {
			Optional<Group> og = gRepo.findById(id);
			if (og.isPresent()) {
				gRepo.delete(og.get().getId());
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
