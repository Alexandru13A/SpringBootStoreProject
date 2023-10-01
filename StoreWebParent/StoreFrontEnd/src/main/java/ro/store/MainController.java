package ro.store;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import ro.store.category.CategoryService;
import ro.store.common.entity.Category;

@Controller
public class MainController {

	@Autowired
	private CategoryService service;

	@GetMapping("")
	public String viewHomePage(Model model) {
		List<Category> categories = service.noChildCategories();
		model.addAttribute("categories", categories);

		return "index";
	}

	@GetMapping("/login")
	public String viewLoginPage(){
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if(authentication == null || authentication instanceof AnonymousAuthenticationToken){
			return "login";
		}
		return "redirect:/";
	}

}
