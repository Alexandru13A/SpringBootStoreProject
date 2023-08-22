package ro.store.admin.category.controller;

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

import ro.store.admin.category.CategoryNotFoundException;
import ro.store.admin.category.CategoryService;
import ro.store.admin.user.util.FileUploadUtil;
import ro.store.common.entity.Category;

@Controller
public class CategoryController {

  private final CategoryService service;

  public CategoryController(CategoryService service) {
    this.service = service;
  }

  @GetMapping("/categories")
  public String getAllCategories(Model model) {
    List<Category> categories = service.getAllCategories();
    model.addAttribute("categories", categories);
    return "/categories/categories";
  }

  // LIST BY PAGE
  @GetMapping("/categories/page/{pageNum}")
  public String listCategoriesByPage(@PathVariable("pageNum") int pageNum, Model model,
      @Param("sortField") String sortField,
      @Param("sortOrder") String sortOrder, @Param("keyword") String keyword) {

    Page<Category> page = service.categoryListingByPage(pageNum, sortField, sortOrder, keyword);
    List<Category> categories = page.getContent();

    long startCount = (pageNum - 1) * CategoryService.CATEGORIES_PER_PAGE + 1;
    long endCount = startCount + CategoryService.CATEGORIES_PER_PAGE - 1;

    if (endCount > page.getTotalElements()) {
      endCount = page.getTotalElements();
    }

    String reverseSortOrder = sortOrder.equals("asc") ? "desc" : "asc";

    model.addAttribute("currentPage", pageNum);
    model.addAttribute("totalPages", page.getTotalPages());
    model.addAttribute("startCount", startCount);
    model.addAttribute("endCount", endCount);
    model.addAttribute("totalItems", page.getTotalElements());
    model.addAttribute("categories", categories);
    model.addAttribute("sortField", sortField);
    model.addAttribute("sortOrder", sortOrder);
    model.addAttribute("keyword", keyword);
    model.addAttribute("reverseSortOrder", reverseSortOrder);

    return "/categories/categories";

  }

  // CATEGORY STATUS UPDATE
  @GetMapping("/categories/{id}/enabled/{status}")
  private String updateCategoryStatus(@PathVariable("id") Integer id, @PathVariable("status") boolean enabled,
      RedirectAttributes redirectAttributes) {

    service.updateStatus(id, enabled);
    String status = enabled ? "enabled" : "disabled";
    String message = "The category with ID :" + id + "has been" + status;
    redirectAttributes.addFlashAttribute("message", message);

    return "redirect:/categories";

  }

  // DELETE CATEGORY
  @GetMapping("/categories/delete/{id}")
  public String deleteCategory(@PathVariable(name = "id") Integer id, RedirectAttributes redirectAttributes,
      Model model) {
    try {
      service.deleteCategory(id);
      redirectAttributes.addFlashAttribute("message",
          "The Category with ID :" + id + " has been deleted successfully");

    } catch (CategoryNotFoundException e) {
      redirectAttributes.addFlashAttribute("message", e.getMessage());
    }
    return "redirect:/categories";

  }

  // CREATE NEW CATEGORY
  @GetMapping("/categories/new")
  public String newCategory(Model model) {
    Category category = new Category();
    List<Category> categories = service.listCategoriesUsedInForm();
    model.addAttribute("category", category);
    model.addAttribute("categories", categories);
    model.addAttribute("pageTitle", "Create new Category");
    return "/categories/category_form";
  }
  // SAVE CATEGORY

  @PostMapping("/categories/save")
  public String saveCategory(Category category, RedirectAttributes redirectAttributes,
      @RequestParam("fileImage") MultipartFile multipartFile)
      throws IOException {
    if (!multipartFile.isEmpty()) {
      String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
      category.setImage(fileName);

      Category savedCategory = service.saveCategory(category);
      String uploadDirectory = "category-images/" + savedCategory.getId();
      FileUploadUtil.saveFile(uploadDirectory, fileName, multipartFile);
    } else {
      service.saveCategory(category);
    }
    redirectAttributes.addFlashAttribute("message", "Category saved successfully");
    return "redirect:/categories";

  }

  private String getRedirectUrlToAffectedCategory(Category category) {
    String namePath = category.getName();
    return "redirect:/categories/page/1?sortField=id&sortOrder=asc&keyword=" + namePath;
  }

  // EDIT CATEGORY
  @GetMapping("/categories/edit/{id}")
  public String editCategory(@PathVariable("id") Integer id, Model model, RedirectAttributes redirectAttributes) {

    try {
      Category category = service.editCategoryById(id);
      List<Category> categories = service.listCategoriesUsedInForm();
      model.addAttribute("category", category);
      model.addAttribute("categories", categories);
      model.addAttribute("pageTitle", "Create new Category");
      return "/categories/category_form";
    } catch (CategoryNotFoundException e) {
      redirectAttributes.addFlashAttribute("message", e.getMessage());
      return "redirect:/categories";
    }

  }

}