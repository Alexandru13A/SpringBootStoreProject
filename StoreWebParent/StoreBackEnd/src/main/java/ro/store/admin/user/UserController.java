package ro.store.admin.user;

import java.io.IOException;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpServletResponse;
import ro.store.admin.util.FileUploadUtil;
import ro.store.admin.util.UserCsvExporter;
import ro.store.admin.util.UserExcelExporter;
import ro.store.common.entity.Role;
import ro.store.common.entity.User;

@Controller
public class UserController {

	private final UserService service;

	public UserController(UserService service) {
		this.service = service;
	}

	@GetMapping("/users")
	public String listFirstPage(Model model) {
		return listByPage(1, model, "firstName", "asc", null);
	}

	@GetMapping("/users/page/{pageNum}")
	public String listByPage(@PathVariable("pageNum") int pageNum, Model model, @Param("sortField") String sortField,
			@Param("sortOrder") String sortOrder, @Param("keyword") String keyword) {
		Page<User> page = service.listByPage(pageNum, sortField, sortOrder, keyword);

		List<User> users = page.getContent();

		long startCount = (pageNum - 1) * UserService.USERS_PER_PAGE + 1;
		long endCount = startCount + UserService.USERS_PER_PAGE - 1;

		if (endCount > page.getTotalElements()) {
			endCount = page.getTotalElements();
		}

		String reverseSortOrder = sortOrder.equals("asc") ? "desc" : "asc";

		model.addAttribute("currentPage", pageNum);
		model.addAttribute("totalPages", page.getTotalPages());
		model.addAttribute("startCount", startCount);
		model.addAttribute("endCount", endCount);
		model.addAttribute("totalItems", page.getTotalElements());
		model.addAttribute("users", users);
		model.addAttribute("sortField", sortField);
		model.addAttribute("sortOrder", sortOrder);
		model.addAttribute("keyword", keyword);
		model.addAttribute("reverseSortOrder", reverseSortOrder);

		return "users";
	}

	@GetMapping("/users/new")
	public String newUser(Model model) {
		List<Role> rolesList = service.rolesList();
		User user = new User();
		user.setEnabled(true);

		model.addAttribute("user", user);
		model.addAttribute("rolesList", rolesList);
		model.addAttribute("pageTitle", "Create new User");

		return "user_form";
	}

	@PostMapping("/users/save")
	private String saveUser(User user, RedirectAttributes redirectAttributes,
			@RequestParam("image") MultipartFile multipartFile) throws IOException {

		if (!multipartFile.isEmpty()) {
			String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
			user.setPhoto(fileName);
			User savedUser = service.saveUser(user);

			String uploadDir = "user-photo/" + savedUser.getId();

			FileUploadUtil.cleanDirectory(uploadDir);
			FileUploadUtil.saveFile(uploadDir, fileName, multipartFile);
		} else {
			if (user.getPhoto().isEmpty())
				user.setPhoto(null);
			service.saveUser(user);
		}

		redirectAttributes.addFlashAttribute("message", "The user has been saved  seuccessfully");
		return getRedirectUrlToAffectedUser(user);
	}

	private String getRedirectUrlToAffectedUser(User user) {
		String firstPathOfEmail = user.getEmail().split("@")[0];
		return "redirect:/users/page/1?sortField=id&sortOrder=asc&keyword=" + firstPathOfEmail;
	}

	@GetMapping("/users/edit/{id}")
	public String editUser(@PathVariable(name = "id") Integer id, RedirectAttributes redirectAttributes, Model model) {
		try {
			User user = service.getUserById(id);
			List<Role> rolesList = service.rolesList();

			model.addAttribute("user", user);
			model.addAttribute("rolesList", rolesList);
			model.addAttribute("pageTitle", "Edit User (ID: " + id + " )");
			return "user_form";
		} catch (UserNotFoundException e) {
			redirectAttributes.addFlashAttribute("message", e.getMessage());
			return "redirect:/users";
		}

	}

	@GetMapping("/users/delete/{id}")
	public String deleteUser(@PathVariable(name = "id") Integer id, RedirectAttributes redirectAttributes,
			Model model) {

		try {
			service.delete(id);
			redirectAttributes.addFlashAttribute("message",
					"The user with ID : " + id + " has been deleted successfully");
		} catch (UserNotFoundException e) {
			redirectAttributes.addFlashAttribute("message", e.getMessage());
		}
		return "redirect:/users";

	}

	@GetMapping("/users/{id}/enabled/{status}")
	public String updateUserStatus(@PathVariable("id") Integer id, @PathVariable("status") boolean enabled,
			RedirectAttributes redirectAttributes) {

		service.updateStatus(id, enabled);
		String status = enabled ? "enabled" : "disabled";
		String message = "The user ID " + id + " has been" + status;
		redirectAttributes.addFlashAttribute("message", message);

		return "redirect:/users";
	}

	@GetMapping("/users/export/csv")
	public void exportToCSV(HttpServletResponse response) throws IOException {
		List<User> listUsers = service.usersList();
		UserCsvExporter exporter = new UserCsvExporter();
		exporter.export(listUsers, response);

	}

	@GetMapping("/users/export/excel")
	public void exportToExcel(HttpServletResponse response) throws IOException {
		List<User> listUsers = service.usersList();
		UserExcelExporter exporter = new UserExcelExporter();
		exporter.export(listUsers, response);
	}

}
