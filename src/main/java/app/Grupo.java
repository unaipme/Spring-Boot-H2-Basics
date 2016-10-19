package app;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;


@Entity
public class Grupo {
	
	@Id
	@GeneratedValue
	private Integer id;
	private @Column(nullable=false, name="groupName") String name;
	@OneToMany(mappedBy="group")
	private List<Persona> members;
	
	protected Grupo() {}
	
	public Grupo(String name) {
		this.name = name;
		this.members = new ArrayList<Persona>();
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<Persona> getMembers() {
		return members;
	}

	public void setMembers(List<Persona> members) {
		this.members = members;
	}
	
}
