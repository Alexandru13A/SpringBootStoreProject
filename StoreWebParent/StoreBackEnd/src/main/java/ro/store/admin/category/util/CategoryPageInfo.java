package ro.store.admin.category.util;

public class CategoryPageInfo {

  private int totalPages;
  private long totalElements;


  public CategoryPageInfo(int totalPages, int totalElements) {
    this.totalPages = totalPages;
    this.totalElements = totalElements;
  }


  public CategoryPageInfo() {
  }



  public int getTotalPages() {
    return this.totalPages;
  }

  public void setTotalPages(int totalPages) {
    this.totalPages = totalPages;
  }

  public long getTotalElements() {
    return this.totalElements;
  }

  public void setTotalElements(long totalElements) {
    this.totalElements = totalElements;
  }

  
}
