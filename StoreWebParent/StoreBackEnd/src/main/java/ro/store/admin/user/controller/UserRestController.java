package ro.store.admin.user.controller;

import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import ro.store.admin.user.UserService;

@RestController
public class UserRestController {

	private final UserService service;

	public UserRestController(UserService service) {
		this.service = service;
	}

	@PostMapping("/users/check_email")
	public String checkForUniqueEmail(@Param("email") String email,@Param("id")Integer id) {
		return service.checkForUniqueEmail(id,email) ? "OK" : "Duplicated Email";
	}

}
