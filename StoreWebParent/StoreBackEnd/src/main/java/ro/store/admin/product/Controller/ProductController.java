package ro.store.admin.product.Controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import ro.store.admin.brand.BrandService;
import ro.store.admin.product.ProductNotFoundException;
import ro.store.admin.product.ProductService;
import ro.store.admin.user.util.FileUploadUtil;
import ro.store.common.entity.Brand;
import ro.store.common.entity.Product.Product;
import ro.store.common.entity.Product.ProductImage;

@Controller
public class ProductController {
  private static final Logger LOGGER = LoggerFactory.getLogger(ProductController.class);
  @Autowired
  private ProductService productService;
  @Autowired
  private BrandService brandService;

  @GetMapping("/products")
  public String productsList(Model model) {
    List<Product> products = productService.productsList();
    model.addAttribute("products", products);
    return "/products/products";
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
      @RequestParam("fileImage") MultipartFile mainImageMultipart,
      @RequestParam("extraImage") MultipartFile[] extraImageMultipart,
      @RequestParam(name = "detailIDs", required = false) String[] detailIDs,
      @RequestParam(name = "detailNames", required = false) String[] detailNames,
      @RequestParam(name = "detailValues", required = false) String[] detailValues,
      @RequestParam(name = "imageIDs", required = false) String[] imagesIDs,
      @RequestParam(name = "imageNames", required = false) String[] imageNames) throws IOException {

    setMainImageName(mainImageMultipart, product);
    setExistingExtraImageNames(imagesIDs, imageNames, product);
    setNewExtraImageNames(extraImageMultipart, product);
    setProductDetails(detailIDs, detailNames, detailValues, product);

    Product savedProduct = productService.saveProduct(product);

    saveUploadedImages(mainImageMultipart, extraImageMultipart, savedProduct);

    deleteExtraImagesWereRemovedOnForm(product);

    redirectAttributes.addFlashAttribute("message", "The product has been saved successfully");
    return "redirect:/products";
  }

  private void deleteExtraImagesWereRemovedOnForm(Product product) {
    String extraImageDirectory = "product-images/" + product.getId() + "/extras";
    Path directoryPath = Paths.get(extraImageDirectory);

    try {
      Files.list(directoryPath).forEach(file -> {
        String fileName = file.toFile().getName();

        if (!product.containsImageName(fileName)) {
          try {
            Files.delete(file);
            LOGGER.info("Deleted extra image: " + fileName);
          } catch (IOException e) {
            LOGGER.error("Could not delete extra image: " + fileName);
          }
        }
      });
    } catch (IOException e) {
      LOGGER.error("Could not list directory: " + directoryPath);
    }

  }

  private void setExistingExtraImageNames(String[] imagesIDs, String[] imageNames, Product product) {
    if (imagesIDs == null || imagesIDs.length == 0)
      return;

    Set<ProductImage> images = new HashSet<>();

    for (int count = 0; count < imagesIDs.length; count++) {
      Integer id = Integer.parseInt(imagesIDs[count]);
      String name = imageNames[count];
      images.add(new ProductImage(id, name, product));
    }
    product.setImages(images);
  }

  private void setProductDetails(String[] detailIDs, String[] detailNames, String[] detailValues, Product product) {
    if (detailNames == null || detailNames.length == 0)
      return;

    for (int count = 0; count < detailNames.length; count++) {
      String name = detailNames[count];
      String value = detailValues[count];
      Integer id = Integer.parseInt(detailIDs[count]);

      if (id != 0) {
        product.addDetails(id, name, value);
      } else if (!name.isEmpty() && !value.isEmpty()) {
        product.addDetails(name, value);
      }
    }
  }

  // this method will save and create the folder for the extra images
  private void saveUploadedImages(MultipartFile mainImageMultipart, MultipartFile[] extraImageMultipart,
      Product savedProduct) throws IOException {

    if (!mainImageMultipart.isEmpty()) {
      String fileName = StringUtils.cleanPath(mainImageMultipart.getOriginalFilename());
      String uploadDirectory = "product-images/" + savedProduct.getId();

      FileUploadUtil.cleanDirectory(uploadDirectory);
      FileUploadUtil.saveFile(uploadDirectory, fileName, mainImageMultipart);
    }

    if (extraImageMultipart.length > 0) {
      String uploadDirectory = "product-images/" + savedProduct.getId() + "/extras";

      for (MultipartFile multipartFile : extraImageMultipart) {
        if (multipartFile.isEmpty())
          continue;

        String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
        FileUploadUtil.saveFile(uploadDirectory, fileName, multipartFile);
      }
    }
  }

  // this method will set the images for the product that will be saved
  private void setNewExtraImageNames(MultipartFile[] extraImageMultipart, Product product) {
    if (extraImageMultipart.length > 0) {
      for (MultipartFile multipartFile : extraImageMultipart) {
        if (!multipartFile.isEmpty()) {
          String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
          if (!product.containsImageName(fileName)) {
            product.addExtraImage(fileName);
          }
        }
      }
    }
  }

  // This method will set the main image that will be saved
  private void setMainImageName(MultipartFile mainImageMultipart, Product product) {
    if (!mainImageMultipart.isEmpty()) {
      String fileName = StringUtils.cleanPath(mainImageMultipart.getOriginalFilename());
      product.setMainImage(fileName);
    }
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