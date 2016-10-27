package app;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;


@Entity
@Table(name = "groups")
public class Group {
	
	@Id
	@GeneratedValue
	private Integer id;
	
	@Column(nullable=false, name="group_name")
	private String name;
	
	@OneToMany(mappedBy="group")
	private List<Person> members;
	
	protected Group() {}
	
	public Group(String name) {
		this.name = name;
		this.members = new ArrayList<>();
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

	public List<Person> getMembers() {
		return members;
	}

	public void setMembers(List<Person> members) {
		this.members = members;
	}
	
}