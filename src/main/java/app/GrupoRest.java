package app;

import java.util.List;

import org.apache.commons.collections.IteratorUtils;
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
@RequestMapping("/group")
public class GrupoRest {
	
	@Autowired
	private GrupoRepo gRepo;
	
	@Autowired
	private PersonaRepo pRepo;
	
	@SuppressWarnings("unchecked")
	@GetMapping
	public List<Grupo> getAll() {
		return IteratorUtils.toList(gRepo.findAll().iterator());
	}
	
	@GetMapping(value="/{id}")
	public Grupo getOne(@PathVariable Integer id) {
		return gRepo.findOne(id);
	}
	
	@GetMapping(value="/{id}/members")
	public List<Persona> getMembers(@PathVariable Integer id) {
		return gRepo.findOne(id).getMembers();
	}
	
	@PutMapping(params={"gid", "pid"})
	public String addMember(@RequestParam(value="gid") Integer gid,
							@RequestParam(value="pid") Integer pid) {
		try {
			Persona p = pRepo.findOne(pid);
			p.setGroup(gRepo.findOne(gid));
			pRepo.save(p);			
			return "Everything OK!";
		} catch (Exception e) {
			return e.getMessage();
		}
	}
	
	@PutMapping("/{id}")
	public String update(@RequestParam(value="name", required=false) String name,
						@PathVariable Integer id) {
		try {
			Grupo g = gRepo.findOne(id);
			if (name != null) g.setName(name);
			gRepo.save(g);
			return "Everything OK!";
		} catch (Exception e) {
			return e.getMessage();
		}
	}
	
	@PostMapping(params={"name"})
	public String create(@RequestParam(value="name") String name) {
		try {
			Grupo g = new Grupo(name);
			gRepo.save(g);
			return "Everything OK!";
		} catch (Exception e) {
			return e.getMessage();
		}
	}
	
	@DeleteMapping(params={"id"})
	public String delete(@RequestParam(value="id") Integer id) {
		try {
			gRepo.delete(id);
			return "Everything OK!";
		} catch (Exception e) {
			return e.getMessage();
		}
	}
	
}
