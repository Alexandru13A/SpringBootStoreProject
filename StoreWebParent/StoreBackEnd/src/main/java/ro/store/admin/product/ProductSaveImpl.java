package ro.store.admin.product;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import ro.store.admin.aws.AmazonS3Util;
import ro.store.admin.user.util.FileUploadUtil;
import ro.store.common.entity.product.Product;
import ro.store.common.entity.product.ProductImage;

public class ProductSaveImpl {
  private static final Logger LOGGER = LoggerFactory.getLogger(ProductSaveImpl.class);

  public static void deleteExtraImagesWereRemovedOnForm(Product product) {
    String extraImageDirectory = "product-images/" + product.getId() + "/extras";

    List<String> listObjectKeys = AmazonS3Util.listFolder(extraImageDirectory);

    for (String objectKey : listObjectKeys) {
      int lastIndexOfSlash = objectKey.lastIndexOf("/");
      String fileName = objectKey.substring(lastIndexOfSlash + 1, objectKey.length());

      if (!product.containsImageName(fileName)) {
        AmazonS3Util.deleteFile(objectKey);
      }
    }

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

  public static void setExistingExtraImageNames(String[] imagesIDs, String[] imageNames, Product product) {
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

  public static void setProductDetails(String[] detailIDs, String[] detailNames, String[] detailValues,
      Product product) {
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
  public static void saveUploadedImages(MultipartFile mainImageMultipart, MultipartFile[] extraImageMultipart,
      Product savedProduct) throws IOException {

    if (!mainImageMultipart.isEmpty()) {
      String fileName = StringUtils.cleanPath(mainImageMultipart.getOriginalFilename());
      String uploadDirectory = "product-images/" + savedProduct.getId();

      FileUploadUtil.cleanDirectory(uploadDirectory);
      FileUploadUtil.saveFile(uploadDirectory, fileName, mainImageMultipart);

      String uploadDirectoryAWS = "product-images/" + savedProduct.getId();

      List<String> listObjectKeys = AmazonS3Util.listFolder(uploadDirectoryAWS + "/");
      for (String objectKey : listObjectKeys) {
        if (!objectKey.contains("/extras/")) {
          AmazonS3Util.deleteFile(objectKey);
        }
      }

      AmazonS3Util.uploadFile(uploadDirectoryAWS, fileName, mainImageMultipart.getInputStream());
    }

    if (extraImageMultipart.length > 0) {
      String uploadDirectory = "product-images/" + savedProduct.getId() + "/extras";

      for (MultipartFile multipartFile : extraImageMultipart) {
        if (multipartFile.isEmpty())
          continue;

        String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
        AmazonS3Util.uploadFile(uploadDirectory, fileName, multipartFile.getInputStream());
        FileUploadUtil.saveFile(uploadDirectory, fileName, multipartFile);
      }
    }
  }

  // this method will set the images for the product that will be saved
  public static void setNewExtraImageNames(MultipartFile[] extraImageMultipart, Product product) {
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
  public static void setMainImageName(MultipartFile mainImageMultipart, Product product) {
    if (!mainImageMultipart.isEmpty()) {
      String fileName = StringUtils.cleanPath(mainImageMultipart.getOriginalFilename());
      product.setMainImage(fileName);
    }
  }

}