package ro.store.admin.user.controller;

import java.io.IOException;
import java.util.List;

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
import ro.store.admin.aws.AmazonS3Util;
import ro.store.admin.common.paging.PagingAndSortingHelper;
import ro.store.admin.common.paging.PagingAndSortingParam;
import ro.store.admin.user.UserNotFoundException;
import ro.store.admin.user.UserService;
import ro.store.admin.user.util.UserCsvExporter;
import ro.store.admin.user.util.UserExcelExporter;
import ro.store.common.entity.Role;
import ro.store.common.entity.User;

@Controller
public class UserController {

	private String defaultRedirectURL = "redirect:/users/page/1?sortField=firstName&sortDir=asc";
	private final UserService service;

	public UserController(UserService service) {
		this.service = service;
	}

	@GetMapping("/users")
	public String listFirstPage() {
		return defaultRedirectURL;
	}

	// LIST USERS BY PAGE
	@GetMapping("/users/page/{pageNum}")
	public String listByPage(
			@PagingAndSortingParam(listName = "users", moduleURL = "/users") PagingAndSortingHelper helper,
			@PathVariable(name = "pageNum") int pageNum) {
		service.listByPage(pageNum, helper);
		return "users/users";
	}

	@GetMapping("/users/new")
	public String newUser(Model model) {
		List<Role> rolesList = service.rolesList();
		User user = new User();
		user.setEnabled(true);

		model.addAttribute("user", user);
		model.addAttribute("rolesList", rolesList);
		model.addAttribute("pageTitle", "Create new User");

		return "users/user_form";
	}

	@PostMapping("/users/save")
	private String saveUser(User user, RedirectAttributes redirectAttributes,
			@RequestParam("image") MultipartFile multipartFile) throws IOException {

		if (!multipartFile.isEmpty()) {
			String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
			user.setPhoto(fileName);
			User savedUser = service.saveUser(user);

			//SAVE USER PHOTO IN AWS S3 DATABASE
			String uploadPhotoOnAWS = "user-photo/" + savedUser.getId();
			AmazonS3Util.deleteFolder(uploadPhotoOnAWS);
			AmazonS3Util.uploadFile(uploadPhotoOnAWS, fileName, multipartFile.getInputStream());


			//SAVE USER PHOTO ON SERVER
			// String uploadDir = "user-photo/" + savedUser.getId();
			// FileUploadUtil.cleanDirectory(uploadDir);
			// FileUploadUtil.saveFile(uploadDir, fileName, multipartFile);


		} else {
			if (user.getPhoto().isEmpty())
				user.setPhoto(null);
			service.saveUser(user);
		}

		redirectAttributes.addFlashAttribute("message", "The user has been saved  successfully");
		return getRedirectUrlToAffectedUser(user);
	}

	private String getRedirectUrlToAffectedUser(User user) {
		String firstPathOfEmail = user.getEmail().split("@")[0];
		return "redirect:/users/page/1?sortField=id&sortDir=asc&keyword=" + firstPathOfEmail;
	}

	@GetMapping("/users/edit/{id}")
	public String editUser(@PathVariable(name = "id") Integer id, RedirectAttributes redirectAttributes, Model model) {
		try {
			User user = service.getUserById(id);
			List<Role> rolesList = service.rolesList();

			model.addAttribute("user", user);
			model.addAttribute("rolesList", rolesList);
			model.addAttribute("pageTitle", "Edit User (ID: " + id + " )");
			return "users/user_form";
		} catch (UserNotFoundException e) {
			redirectAttributes.addFlashAttribute("message", e.getMessage());
			return defaultRedirectURL;
		}

	}

	@GetMapping("/users/delete/{id}")
	public String deleteUser(@PathVariable(name = "id") Integer id, RedirectAttributes redirectAttributes,
			Model model) {

		try {
			service.delete(id);
			
			String userPhotoPathAWS = "user-photo/"+id;
			AmazonS3Util.deleteFolder(userPhotoPathAWS);

			redirectAttributes.addFlashAttribute("message",
					"The user with ID : " + id + " has been deleted successfully");
		} catch (UserNotFoundException e) {
			redirectAttributes.addFlashAttribute("message", e.getMessage());
		}
		return defaultRedirectURL;

	}

	@GetMapping("/users/{id}/enabled/{status}")
	public String updateUserStatus(@PathVariable("id") Integer id, @PathVariable("status") boolean enabled,
			RedirectAttributes redirectAttributes) {

		service.updateStatus(id, enabled);
		String status = enabled ? "enabled" : "disabled";
		String message = "The user ID " + id + " has been " + status;
		redirectAttributes.addFlashAttribute("message", message);

		return defaultRedirectURL;
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
