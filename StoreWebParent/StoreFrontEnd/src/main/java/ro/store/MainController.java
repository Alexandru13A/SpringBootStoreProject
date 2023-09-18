package ro.store;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
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

}
