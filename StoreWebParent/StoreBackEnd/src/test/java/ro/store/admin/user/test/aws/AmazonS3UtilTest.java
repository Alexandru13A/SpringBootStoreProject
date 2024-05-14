package ro.store.admin.user.test.aws;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.List;

import org.junit.jupiter.api.Test;

import ro.store.admin.aws.AmazonS3Util;

public class AmazonS3UtilTest {

  @Test
  public void testListFolder() {
    String folderName = "product-images/1";
    List<String> listKeys = AmazonS3Util.listFolder(folderName);
    listKeys.forEach(System.out::println);
  }

  @Test
  public void testUploadFile() throws FileNotFoundException {
    String folderName = "test-upload/one/two/three";
    String fileName = "Matthew.png";
    String filePath = "F:\\Desktop\\" + fileName;

    InputStream inputStream = new FileInputStream(filePath);

    AmazonS3Util.uploadFile(folderName, fileName, inputStream);
  }

  @Test
  public void testDeleteFile() {
    String fileName = "test-upload/one/two/three/Matthew.png";
    AmazonS3Util.deleteFile(fileName);
  }

  @Test
  public void testDeleteFolder() {
    String folderName = "test-upload";
    AmazonS3Util.deleteFolder(folderName);

  }
}
