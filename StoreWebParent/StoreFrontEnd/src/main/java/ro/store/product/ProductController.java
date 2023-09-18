package ro.store.product;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import ro.store.category.CategoryService;
import ro.store.common.entity.Category;
import ro.store.common.entity.Product.Product;
import ro.store.common.exception.category.CategoryNotFoundException;

@Controller
public class ProductController {

  @Autowired
  private CategoryService categoryService;
  @Autowired
  private ProductService productService;

  @GetMapping("/c/{category_alias}")
  public String viewCategotyFirstPage(@PathVariable("category_alias") String alias,
      Model model) {
    return viewCategoryByPage(alias, 1, model);

  }

  @GetMapping("/c/{category_alias}/page/{pageNum}")
  public String viewCategoryByPage(@PathVariable("category_alias") String alias,
      @PathVariable("pageNum") int pageNum,
      Model model) {

    try {
      Category category = categoryService.getCategory(alias);
      List<Category> listCategoryParents = categoryService.getCategoryParents(category);

      Page<Product> pageProducts = productService.listByCategory(pageNum, category.getId());
      List<Product> products = pageProducts.getContent();

      long startCount = (pageNum - 1) * ProductService.PRODUCT_PER_PAGE_FRONTEND + 1;
      long endCount = startCount + ProductService.PRODUCT_PER_PAGE_FRONTEND - 1;
      if (endCount > pageProducts.getTotalElements()) {
        endCount = pageProducts.getTotalElements();
      }

      model.addAttribute("currentPage", pageNum);
      model.addAttribute("totalPages", pageProducts.getTotalPages());
      model.addAttribute("startCount", startCount);
      model.addAttribute("endCount", endCount);
      model.addAttribute("totalItems", pageProducts.getTotalElements());

      model.addAttribute("pageTitle", category.getName());
      model.addAttribute("listCategoryParents", listCategoryParents);
      model.addAttribute("products", products);
      model.addAttribute("category_alias", category.getAlias());
      model.addAttribute("category", category);
      return "product/products_by_category";
    } catch (CategoryNotFoundException e) {
      return "error/404";
    }
  }

  @GetMapping("/p/{product_alias}")
  public String viewProductDetails(@PathVariable("product_alias") String alias, Model model) {
    try {
      Product product = productService.getProductByAlias(alias);
      List<Category> listCategoryParents = categoryService.getCategoryParents(product.getCategory());
      model.addAttribute("listCategoryParents", listCategoryParents);
      model.addAttribute("product", product);
      model.addAttribute("pageTitle", product.getShortName());
      return "product/product_detail";
    } catch (Exception e) {
      return "error/404";

    }
  }

  @GetMapping("/search")
  public String searchFirstPage(@Param("keyword") String keyword, Model model) {
    return searchProductByPage(keyword, 1, model);
  }

  @GetMapping("/search/page/{pageNum}")
  public String searchProductByPage(@Param("keyword") String keyword,
      @PathVariable("pageNum") int pageNum, Model model) {
    Page<Product> pageProducts = productService.searchProduct(keyword, pageNum);
    List<Product> products = pageProducts.getContent();

    long startCount = (pageNum - 1) * ProductService.SEARCH_RESULT_PER_PAGE + 1;
    long endCount = startCount + ProductService.SEARCH_RESULT_PER_PAGE - 1;
    if (endCount > pageProducts.getTotalElements()) {
      endCount = pageProducts.getTotalElements();
    }

    model.addAttribute("currentPage", pageNum);
    model.addAttribute("totalPages", pageProducts.getTotalPages());
    model.addAttribute("startCount", startCount);
    model.addAttribute("endCount", endCount);
    model.addAttribute("totalItems", pageProducts.getTotalElements());
    model.addAttribute("pageTitle", keyword + " -Search Result ");

    model.addAttribute("keyword", keyword);
    model.addAttribute("products", products);

    return "product/product_search_result";
  }
}
