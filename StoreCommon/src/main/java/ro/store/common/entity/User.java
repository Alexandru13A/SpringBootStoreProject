package ro.store.common.entity;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
public class User {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@Column(length = 128, nullable = false, unique = true)
	private String email;

	@Column(length = 64, nullable = false)
	private String password;

	@Column(name = "first_name", length = 64, nullable = false)
	private String firstName;

	@Column(name = "last_name", length = 64, nullable = false)
	private String lastName;

	@Column(name = "user_photo")
	private String photo;

	private boolean enabled;

	@ManyToMany(fetch = FetchType.EAGER)
	@JoinTable(name = "users_roles", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "role_id"))
	private Set<Role> roles = new HashSet<>();

	public User(String email, String password, String firstName, String lastName) {
		super();
		this.email = email;
		this.password = password;
		this.firstName = firstName;
		this.lastName = lastName;
	}

	public Set<Role> getRoles() {
		return roles;
	}

	public void setRoles(Set<Role> roles) {
		this.roles = roles;
	}

	public void addRole(Role role) {
		this.roles.add(role);
	}

	@Override
	public String toString() {
		return "User [id=" + id + ", email=" + email + ", password=" + password + ", firstName=" + firstName
				+ ", lastName=" + lastName + ", roles=" + roles + "]";
	}

	@Transient
	public String getPhotoImagePath() {
		if (id == null || photo == null)
			return "/images/status/portrait.png";

		return "/user-photo/" + this.id + "/" + this.photo;

	}

	public boolean hasRole(String roleName) {
		Iterator<Role> iterator = roles.iterator();
		while (iterator.hasNext()) {
			Role role = iterator.next();
			if (role.getName().equals(roleName)) {
				return true;
			}
		}
		return false;
	}

}
