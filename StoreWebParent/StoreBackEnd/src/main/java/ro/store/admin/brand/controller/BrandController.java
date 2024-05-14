package ro.store.admin.brand.controller;

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

import ro.store.admin.aws.AmazonS3Util;
import ro.store.admin.brand.BrandNotFoundException;
import ro.store.admin.brand.BrandService;
import ro.store.admin.category.CategoryService;
import ro.store.admin.common.paging.PagingAndSortingHelper;
import ro.store.admin.common.paging.PagingAndSortingParam;
import ro.store.admin.user.util.FileUploadUtil;
import ro.store.common.entity.Brand;
import ro.store.common.entity.Category;

@Controller
public class BrandController {

  private String defaultRedirectURL = "redirect:/brands/page/1?sortField=name&sortDir=asc";
  private BrandService brandService;
  private CategoryService catService;

  public BrandController(BrandService brandService, CategoryService catService) {
    this.brandService = brandService;
    this.catService = catService;
  }

  @GetMapping("/brands")
  public String brandsFirstPage(Model model) {
    return defaultRedirectURL;
  }

  @GetMapping("/brands/page/{pageNum}")
  public String listByPage(
      @PagingAndSortingParam(listName = "brands", moduleURL = "/brands") PagingAndSortingHelper helper,
      @PathVariable(name = "pageNum") int pageNum) {
    brandService.listBrandsByPage(pageNum, helper);
    return "brands/brands";
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

      Brand savedBrand = brandService.save(brand);

      String uploadDirectory = "brand-logos/" + savedBrand.getId();

      AmazonS3Util.deleteFolder(uploadDirectory);
      AmazonS3Util.uploadFile(uploadDirectory, fileName, multipartFile.getInputStream());

      FileUploadUtil.cleanDirectory(uploadDirectory);
      FileUploadUtil.saveFile(uploadDirectory, fileName, multipartFile);
    } else {
      brandService.save(brand);
    }

    redirectAttributes.addFlashAttribute("message", "Brand saved successfully");
    return "redirect:/brands";
  }

  @GetMapping("/brands/edit/{id}")
  public String editBrand(@PathVariable("id") Integer id, Model model, RedirectAttributes redirectAttributes) {

    try {
      Brand brand = brandService.editBrandById(id);
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
      brandService.delete(id);
      String brandDir = "/brand-logo/" + id;
      FileUploadUtil.removeDir(brandDir);
      
      String brandDirAWS = "brand-logo/" + id;
      AmazonS3Util.deleteFolder(brandDirAWS);

      redirectAttributes.addFlashAttribute("message", "The Brand with ID: " + id + " has been deleted successfully");

    } catch (BrandNotFoundException e) {
      redirectAttributes.addFlashAttribute("message", e.getMessage());
    }
    return "redirect:/brands";
  }

}