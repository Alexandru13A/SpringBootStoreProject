package ro.store.admin.user.test;

import static org.assertj.core.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.annotation.Rollback;

import ro.store.admin.user.UserRepository;
import ro.store.common.entity.Role;
import ro.store.common.entity.User;

@DataJpaTest(showSql = false)
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Rollback(false)
public class UserRepositoryTest {

	@Autowired
	private UserRepository repo;

	@Autowired
	private TestEntityManager entityManager;

	@Test
	public void testCreateUserWithOneRole() {
		Role roleAdmin = entityManager.find(Role.class, 1);
		User user = new User("alex@gmail.com", "alex1234", "Alexandru", "Pacioianu");
		user.addRole(roleAdmin);

		User savedUser = repo.save(user);
		assertThat(savedUser.getId()).isGreaterThan(0);

	}

	@Test
	public void testCreateUserWithTwoRoles() {

		User userTwo = new User("bogdan@fmail.com", "bogdan2020", "Bogdan", "Mihai");
		Role roleEditor = new Role(3);
		Role roleAssistant = new Role(5);
		userTwo.addRole(roleEditor);
		userTwo.addRole(roleAssistant);

		User savedUser = repo.save(userTwo);
		assertThat(savedUser.getId()).isGreaterThan(0);

	}

	@Test
	public void testListAllUsers() {
		Iterable<User> listUsers = repo.findAll();
		listUsers.forEach(user -> System.out.println(user));
	}

	@Test
	public void getUserById() {
		User user = repo.findById(1).get();
		System.out.println(user);
		assertThat(user).isNotNull();
	}

	@Test
	public void updateUserDetailsTest() {
		User userToUpdate = repo.findById(1).get();
		userToUpdate.setEnabled(true);
		userToUpdate.setEmail("admin@gmail.com");

		repo.save(userToUpdate);
	}

	@Test
	public void userRoleUpdateTest() {
		User user = repo.findById(3).get();
		Role roleSalesperson = new Role(3);
		Role roleEditor = new Role(4);

		user.getRoles().remove(roleSalesperson);
		user.addRole(roleEditor);

		repo.save(user);
	}

	@Test
	public void deleteUserTest() {
		Integer userId = 2;
		repo.deleteById(userId);

		repo.findById(userId);
	}

	@Test
	public void testGetUserByEmail() {
		String email = "admin@gmail.com";
		User user = repo.getUserByEmail(email);

		assertThat(user).isNotNull();
	}

	@Test
	public void testCountById() {
		Integer id = 1;
		Long countById = repo.countById(id);

		assertThat(countById).isNotNull().isGreaterThan(0);
	}

	@Test
	public void disableTestStatusUpdate() {
		Integer id = 1;
		repo.updateStatus(id, false);
	}

	@Test
	public void enableTestStatusUpdate() {
		Integer id = 1;
		repo.updateStatus(id, true);
	}

	@Test
	public void testSearchUsers() {
		String keyword = "maria";

		int pageNumber = 0;
		int pageSize = 4;

		PageRequest pageable = PageRequest.of(pageNumber, pageSize);
		Page<User> page = repo.findAll(keyword, pageable);

		List<User> listUsers = page.getContent();

		listUsers.forEach(user -> System.out.println(user));

		assertThat(listUsers.size()).isGreaterThan(0);
	}

}
