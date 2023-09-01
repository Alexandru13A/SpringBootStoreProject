package ro.store.admin.barnd.controller;

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

import ro.store.admin.barnd.BrandNotFoundException;
import ro.store.admin.barnd.BrandService;
import ro.store.admin.category.CategoryService;
import ro.store.admin.user.util.FileUploadUtil;
import ro.store.common.entity.Brand;
import ro.store.common.entity.Category;

@Controller
public class BrandController {

  private BrandService service;
  private CategoryService catService;

  public BrandController(BrandService service, CategoryService catService) {
    this.service = service;
    this.catService = catService;
  }

  @GetMapping("/brands")
  public String brandsFirstPage(Model model) {
    return listBrandsByPage(1, model, "name", "asc", null);
  }

  @GetMapping("/brands/page/{pageNum}")
  public String listBrandsByPage(@PathVariable("pageNum") int pageNum, Model model,
      @Param("sortField") String sortField,
      @Param("sortOrder") String sortOrder, @Param("keyword") String keyword) {

    Page<Brand> page = service.listBrandsByPage(pageNum, sortField, sortOrder, keyword);

    List<Brand> brands = page.getContent();

    long startCount = (pageNum - 1) * BrandService.BRAND_PER_PAGE + 1;
    long endCount = startCount + BrandService.BRAND_PER_PAGE - 1;

    if (endCount > page.getTotalElements()) {
      endCount = page.getTotalElements();
    }

    String reverseSortOrder = sortOrder.equals("asc") ? "desc" : "asc";

    model.addAttribute("currentPage", pageNum);
    model.addAttribute("totalPages", page.getTotalPages());
    model.addAttribute("startCount", startCount);
    model.addAttribute("endCount", endCount);
    model.addAttribute("totalItems", page.getTotalElements());
    model.addAttribute("brands", brands);
    model.addAttribute("sortField", sortField);
    model.addAttribute("sortOrder", sortOrder);
    model.addAttribute("keyword", keyword);
    model.addAttribute("reverseSortOrder", reverseSortOrder);

    return "/brands/brands";
  }

  @GetMapping("/brands/new")
  public String newBrand(Model model) {
    Brand brand = new Brand();
    List<Category> listCategories = catService.listCategoriesUsedInForm();
    model.addAttribute("brand", brand);
    model.addAttribute("listCategories", listCategories);
    model.addAttribute("pageTitle", "Create new Brand");
    return "/brands/brand_form";
  }

  @PostMapping("/brands/save")
  public String saveBrand(Brand brand, RedirectAttributes redirectAttributes,
      @RequestParam("fileImage") MultipartFile multipartFile) throws IOException {

    if (!multipartFile.isEmpty()) {
      String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
      brand.setLogo(fileName);

      Brand savedBrand = service.save(brand);
      String uploadDirectory = "brand-logos/" + savedBrand.getId();
      FileUploadUtil.cleanDirectory(uploadDirectory);
      FileUploadUtil.saveFile(uploadDirectory, fileName, multipartFile);
    } else {
      service.save(brand);
    }

    redirectAttributes.addFlashAttribute("message", "Brand saved successfully");
    return "redirect:/brands";
  }

  @GetMapping("/brands/edit/{id}")
  public String editBrand(@PathVariable("id") Integer id, Model model, RedirectAttributes redirectAttributes) {

    try {
      Brand brand = service.editBrandById(id);
      List<Category> listCategories = catService.listCategoriesUsedInForm();

      

      model.addAttribute("brand", brand);
      model.addAttribute("listCategories", listCategories);
      model.addAttribute("pageTitle", "Edit Brand with ID: " + id);
      return "/brands/brand_form";
    } catch (BrandNotFoundException e) {
      redirectAttributes.addFlashAttribute("message", e.getMessage());
      return "redirect:/brands";
    }
  }

  @GetMapping("/brands/delete/{id}")
  public String deleteBrand(@PathVariable("id") Integer id, RedirectAttributes redirectAttributes,
      Model model) {

    try {
      service.delete(id);
      String brandDir = "/brand-logo/" + id;
      FileUploadUtil.removeDir(brandDir);
      redirectAttributes.addFlashAttribute("message", "The Brand with ID: " + id + " has been deleted successfully");

    } catch (BrandNotFoundException e) {
      redirectAttributes.addFlashAttribute("message", e.getMessage());
    }
    return "redirect:/brands";
  }

}