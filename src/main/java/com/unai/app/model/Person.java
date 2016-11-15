package com.unai.app.model;

import java.time.LocalDate;
import java.time.Period;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name="people")
public class Person {
	
	@Id @GeneratedValue
	private Integer id;
	
	@Column(nullable=false, name="first_name")
	private String firstName;
	
	@Column(nullable=false, name="last_name")
	private String lastName;
	
	@Column(nullable=false) @JsonIgnore
	private LocalDate birthday;
	
	@ManyToOne(targetEntity=Group.class) @JsonIgnore
	private Group group;
	
	@SuppressWarnings("unused")
	private int age;
	
	protected Person() {}
	
	public Person(String firstName, String lastName, LocalDate birthday) {
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

	public Group getGroup() {
		return group;
	}

	public void setGroup(Group group) {
		this.group = group;
	}
	
}