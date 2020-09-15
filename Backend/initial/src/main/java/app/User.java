package app;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "users")
public class User {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Integer id;

	@Column(name = "role")
	private String role;

	@Column(name = "username")
	private String username;

	@Column(name = "password")
	private String password;

	@Column(name = "firstname")
	private String firstname;

	@Column(name = "lastname")
	private String lastname;

	@Column(name = "email")
	private String email;
	
	@Column(name = "bio")
	private String bio;
	
	@Column(name = "avatar")
	private String avatar;
	
	@Column(name = "background")
	private String background;

	public User() {

	}

	public User(String role, String username, String password, String firstname, String lastname, String email) {
		this.role = role;
		this.username = username;
		this.password = password;
		this.firstname = firstname;
		this.lastname = lastname;
		this.email = email;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getFirstname() {
		return firstname;
	}

	public void setFirstname(String firstname) {
		this.firstname = firstname;
	}

	public String getLastname() {
		return lastname;
	}

	public void setLastname(String lastname) {
		this.lastname = lastname;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}
	
	public String getBio() {
		return this.bio;
	}

	public void setBio(String bio) {
		this.bio = bio;
	}
	
	public String getAvatar() {
		return this.avatar;
	}

	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}
	
	public String getBackground() {
		return this.background;
	}

	public void setBackground(String background) {
		this.background = background;
	}
	
	@Override
	public String toString() {
		return Application.JSONify(this);
	}

	@Override
	public boolean equals(Object obj) {
		boolean result = false;
		if (obj instanceof User) {
			User newUser = (User) obj;
			if (newUser.getEmail() == this.email && newUser.getFirstname() == this.firstname
					&& newUser.getLastname() == this.lastname && newUser.getPassword() == this.password
					&& newUser.getRole() == this.role && newUser.getUsername() == this.username) {
				result = true;
			}
		}

		return result;

	}
}
