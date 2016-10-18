package app;

import java.time.LocalDate;
import java.time.Period;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
public class Persona {
	
	private @Id @GeneratedValue Integer id;
	private @Column(nullable=false) String firstName;
	private @Column(nullable=false) String lastName;
	private @Column(nullable=false) LocalDate birthday;
	
	private int age;
	
	protected Persona() {}
	
	public Persona(String firstName, String lastName, LocalDate birthday) {
		this.firstName = firstName;
		this.lastName = lastName;
		this.birthday = birthday;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	
	@JsonIgnore
	public LocalDate getBirthday() {
		return birthday;
	}

	public void setBirthday(LocalDate birthday) {
		this.birthday = birthday;
	}

	@Override
	public String toString() {
		return String.format("%s, %s", lastName, firstName);
	}
	
	public int getAge() {
		return Period.between(birthday, LocalDate.now()).getYears();
	}
	
}
