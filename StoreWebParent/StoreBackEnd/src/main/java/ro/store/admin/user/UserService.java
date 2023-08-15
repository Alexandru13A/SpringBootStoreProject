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

	public List<User> usersList() {
		return userRepo.findAll();
	}
	
	public Page<User> listByPage(int pageNum,String sortField,String sortOrder,String keyword){
		Sort sort = Sort.by(sortField);
		
		sort = sortOrder.equals("asc") ? sort.ascending() : sort.descending();
		
		PageRequest pageable = PageRequest.of(pageNum-1, USERS_PER_PAGE,sort);
		
		
		if(keyword != null) {
			return userRepo.findAll(keyword,pageable);
		}
		return userRepo.findAll(pageable);
	}

	public List<Role> rolesList() {
		return roleRepo.findAll();
	}

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

	return	userRepo.save(user);
	}

	private void encodePassword(User user) {
		String encodedPassword = passwordEncoder.encode(user.getPassword());
		user.setPassword(encodedPassword);
	}

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

	public User getUserById(Integer id) throws UserNotFoundException {
		try {
			return userRepo.findById(id).get();
		} catch (NoSuchElementException e) {
			throw new UserNotFoundException("User with this ID: " + id + " doesn't exist ");
		}
	}

	public void delete(Integer id) throws UserNotFoundException {
		Long countById = userRepo.countById(id);

		if (countById == null || countById == 0) {
			throw new UserNotFoundException("Could not found any user with ID " + id);
		}
		userRepo.deleteById(id);
	}

	public void updateStatus(Integer id, boolean enabled) {
		userRepo.updateStatus(id, enabled);
	}

}
