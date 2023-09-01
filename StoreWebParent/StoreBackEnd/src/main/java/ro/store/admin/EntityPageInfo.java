package ro.store.admin;

public class EntityPageInfo {

  private int totalPages;
  private long totalElements;


  public EntityPageInfo(int totalPages, int totalElements) {
    this.totalPages = totalPages;
    this.totalElements = totalElements;
  }


  public EntityPageInfo() {
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
