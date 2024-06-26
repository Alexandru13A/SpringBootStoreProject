package ro.store.admin.product.Controller;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import ro.store.admin.brand.BrandService;
import ro.store.admin.category.CategoryService;
import ro.store.admin.common.paging.PagingAndSortingHelper;
import ro.store.admin.common.paging.PagingAndSortingParam;
import ro.store.admin.product.ProductSaveImpl;
import ro.store.admin.product.ProductService;
import ro.store.admin.user.security.StoreUserDetails;
import ro.store.admin.user.util.FileUploadUtil;
import ro.store.common.entity.Brand;
import ro.store.common.entity.Category;
import ro.store.common.entity.product.Product;
import ro.store.common.exception.product.ProductNotFoundException;

@Controller
public class ProductController {

  private String defaultRedirectURL = "redirect:/products/page/1?sortField=name&sortDir=asc&categoryId=0";

  @Autowired
  private ProductService productService;
  @Autowired
  private BrandService brandService;
  @Autowired
  private CategoryService categoryService;


  @GetMapping("/products")
  public String listFirstPAge(Model model) {
    return defaultRedirectURL;
    //listProductsByPage(1, model, "name", "asc", null, 0);
  }


  @GetMapping("/products/page/{pageNum}")
	public String listByPage(
			@PagingAndSortingParam(listName = "products", moduleURL = "/products") PagingAndSortingHelper helper,
			@PathVariable(name = "pageNum") int pageNum, Model model,
			Integer categoryId
			) {
		
		productService.listProductsByPage(pageNum, helper, categoryId);
		
		List<Category> listCategories = categoryService.listCategoriesUsedInForm();
		
		if (categoryId != null) model.addAttribute("categoryId", categoryId);
		model.addAttribute("categories", listCategories);
		
		return "products/products";		
	}


  // Send the Product object in the form and his assigned brands
  @GetMapping("/products/new")
  public String newProduct(Model model) {
    List<Brand> brands = brandService.getAllBrands();

    Product product = new Product();
    product.setEnabled(true);
    product.setInStock(true);

    Integer numberOfExistingExtraImages = product.getImages().size();

    model.addAttribute("numberOfExistingExtraImages", numberOfExistingExtraImages);
    model.addAttribute("product", product);
    model.addAttribute("brands", brands);
    model.addAttribute("pageTitle", "Create new Product");

    return "/products/product_form";
  }

  // This method will save the product
  @PostMapping("/products/save")
  public String saveProduct(Product product, RedirectAttributes redirectAttributes,
      @RequestParam(value = "fileImage", required = false) MultipartFile mainImageMultipart,
      @RequestParam(value = "extraImage", required = false) MultipartFile[] extraImageMultipart,
      @RequestParam(name = "detailIDs", required = false) String[] detailIDs,
      @RequestParam(name = "detailNames", required = false) String[] detailNames,
      @RequestParam(name = "detailValues", required = false) String[] detailValues,
      @RequestParam(name = "imageIDs", required = false) String[] imagesIDs,
      @RequestParam(name = "imageNames", required = false) String[] imageNames,
      @AuthenticationPrincipal StoreUserDetails loggedUSer) throws IOException {

    if (!loggedUSer.hasRole("Admin") && loggedUSer.hasRole("Editor")) {
      if (loggedUSer.hasRole("Salesperson")) {
        productService.saveProductPrice(product);
        redirectAttributes.addFlashAttribute("message", "The Price has been updated successfully");
        return "redirect:/products";
      }
    }

    ProductSaveImpl.setMainImageName(mainImageMultipart, product);
    ProductSaveImpl.setExistingExtraImageNames(imagesIDs, imageNames, product);
    ProductSaveImpl.setNewExtraImageNames(extraImageMultipart, product);
    ProductSaveImpl.setProductDetails(detailIDs, detailNames, detailValues, product);

    Product savedProduct = productService.saveProduct(product);

    ProductSaveImpl.saveUploadedImages(mainImageMultipart, extraImageMultipart, savedProduct);

    ProductSaveImpl.deleteExtraImagesWereRemovedOnForm(product);

    redirectAttributes.addFlashAttribute("message", "The product has been saved successfully");
    return "redirect:/products";
  }

  @GetMapping("/products/delete/{id}")
  public String deleteProduct(@PathVariable("id") Integer id, RedirectAttributes redirectAttributes,
      Model model) {

    try {
      productService.deleteProduct(id);

      String productExtraImageDir = "product-images/" + id + "/extras";
      String productImageDir = "product-images/" + id;

      FileUploadUtil.removeDir(productExtraImageDir);
      FileUploadUtil.removeDir(productImageDir);

      redirectAttributes.addFlashAttribute("message", "The product with ID: " + id + " has been deleted");
    } catch (ProductNotFoundException e) {
      redirectAttributes.addFlashAttribute("message", e.getMessage());
    }
    return "redirect:/products";

  }

  @GetMapping("/products/{id}/enabled/{status}")
  public String updateProductStatus(@PathVariable("id") Integer id, @PathVariable("status") boolean enabled,
      RedirectAttributes redirect) {

    productService.updateStatus(id, enabled);
    String status = enabled ? "enabled" : "disabled";
    String message = "The product with ID: " + id + " has been " + status;

    redirect.addFlashAttribute("message", message);
    return "redirect:/products";

  }

  @GetMapping("/products/edit/{id}")
  public String editProduct(@PathVariable("id") Integer id, Model model, RedirectAttributes redirectAttributes) {

    try {
      Product product = productService.getProductByID(id);
      List<Brand> brands = brandService.getAllBrands();
      Integer numberOfExistingExtraImages = product.getImages().size();

      model.addAttribute("numberOfExistingExtraImages", numberOfExistingExtraImages);
      model.addAttribute("product", product);
      model.addAttribute("brands", brands);
      model.addAttribute("pageTitle", "Edit Product with ID: " + id);

      return "products/product_form";
    } catch (ProductNotFoundException e) {
      redirectAttributes.addFlashAttribute("message", e.getMessage());
      return "redirect:/products";
    }
  }

  @GetMapping("/products/detail/{id}")
  public String viewProductDetails(@PathVariable("id") Integer id, Model model, RedirectAttributes redirectAttributes) {

    try {
      Product product = productService.getProductByID(id);
      model.addAttribute("product", product);
      return "products/product_detail_modal";

    } catch (ProductNotFoundException e) {
      redirectAttributes.addFlashAttribute("message", e.getMessage());
      return "redirect:/products";
    }
  }

}