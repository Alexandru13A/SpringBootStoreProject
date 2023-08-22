package ro.store.admin.user;

import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ro.store.admin.role.RoleRepository;
import ro.store.common.entity.Role;
import ro.store.common.entity.User;

@Service
@Transactional
public class UserService {
	public static final int USERS_PER_PAGE = 5;
	private final UserRepository userRepo;
	private final RoleRepository roleRepo;
	private final PasswordEncoder passwordEncoder;

	public UserService(UserRepository userRepo, RoleRepository roleRepo, PasswordEncoder passwordEncoder) {
		this.userRepo = userRepo;
		this.roleRepo = roleRepo;
		this.passwordEncoder = passwordEncoder;
	}
	//GET BY EMAIL
	public User getUserByEmail(String email) {
		return userRepo.getUserByEmail(email);
	}

	//GET ALL USERS
	public List<User> usersList() {
		return userRepo.findAll();
	}

	//LIST USERS BY PAGE
	public Page<User> listByPage(int pageNum, String sortField, String sortOrder, String keyword) {
		Sort sort = Sort.by(sortField);

		sort = sortOrder.equals("asc") ? sort.ascending() : sort.descending();

		PageRequest pageable = PageRequest.of(pageNum - 1, USERS_PER_PAGE, sort);

		if (keyword != null) {
			return userRepo.findAll(keyword, pageable);
		}
		return userRepo.findAll(pageable);
	}

	//GET ALL ROLES
	public List<Role> rolesList() {
		return roleRepo.findAll();
	}

	//SAVE USER
	public User saveUser(User user) {
		boolean isUpdatingUser = (user.getId() != null);

		if (isUpdatingUser) {
			User existingUser = userRepo.findById(user.getId()).get();

			if (user.getPassword().isEmpty()) {
				user.setPassword(existingUser.getPassword());
			} else {
				encodePassword(user);
			}
		} else {
			encodePassword(user);
		}
		return userRepo.save(user);
	}

	//UPDATE ACCOUNT INFORMATIONS
	public User updateAccount(User userInForm) {
		User userFromDB = userRepo.findById(userInForm.getId()).get();

		if (!userInForm.getPassword().isEmpty()) {
			userFromDB.setPassword(userInForm.getPassword());
			encodePassword(userFromDB);
		}
		if (userInForm.getPhoto() != null) {
			userFromDB.setPhoto(userInForm.getPhoto());
		}
		userFromDB.setFirstName(userInForm.getFirstName());
		userFromDB.setLastName(userInForm.getLastName());
		return userRepo.save(userFromDB);
	}

	//PASSWORD ENCODER
	private void encodePassword(User user) {
		String encodedPassword = passwordEncoder.encode(user.getPassword());
		user.setPassword(encodedPassword);
	}

	//CHECK  EMAIL TO BE UNIQUE
	public boolean checkForUniqueEmail(Integer id, String email) {
		User userByEmail = userRepo.getUserByEmail(email);

		if (userByEmail == null)
			return true;

		boolean isCreatingNew = (id == null);

		if (isCreatingNew) {
			if (userByEmail != null)
				return false;
		} else {
			if (userByEmail.getId() != id) {
				return false;
			}
		}
		return true;

	}

	//GET USER BY ID
	public User getUserById(Integer id) throws UserNotFoundException {
		try {
			return userRepo.findById(id).get();
		} catch (NoSuchElementException e) {
			throw new UserNotFoundException("User with this ID: " + id + " doesn't exist ");
		}
	}

	//DELETE USER
	public void delete(Integer id) throws UserNotFoundException {
		Long countById = userRepo.countById(id);

		if (countById == null || countById == 0) {
			throw new UserNotFoundException("Could not found any user with ID " + id);
		}
		userRepo.deleteById(id);
	}

	//UPDATE USER STATUS
	public void updateStatus(Integer id, boolean enabled) {
		userRepo.updateStatus(id, enabled);
	}

}
