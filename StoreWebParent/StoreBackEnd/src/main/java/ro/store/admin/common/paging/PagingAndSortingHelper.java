package ro.store.admin.common.paging;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.web.method.support.ModelAndViewContainer;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PagingAndSortingHelper {

  private ModelAndViewContainer model;
  private String listName;
  private String sortField;
  private String sortOrder;
  private String keyword;

  public PagingAndSortingHelper(ModelAndViewContainer model, String listName,String sortField,String sortOrder,String keyword) {
    this.model = model;
    this.listName = listName;
    this.sortField = sortField;
    this.sortOrder = sortOrder;
    this.keyword = keyword;
  }

  public void updateModelAttributes(int pageNum, Page<?> page) {
    List<?> listItems = page.getContent();
    int pageSize = page.getSize();

    long startCount = (pageNum - 1) * pageSize + 1;
    long endCount = startCount + pageSize - 1;

    if (endCount > page.getTotalElements()) {
      endCount = page.getTotalElements();
    }

    model.addAttribute("currentPage", pageNum);
    model.addAttribute("totalPages", page.getTotalPages());
    model.addAttribute("startCount", startCount);
    model.addAttribute("endCount", endCount);
    model.addAttribute("totalItems", page.getTotalElements());
    model.addAttribute(listName, listItems);
  }

  public void listEntities(int pageNum, int pageSize,SearchRepository<?,Integer> repo ){
    Sort sort = Sort.by(sortField);
    sort = sortOrder.equals("asc") ? sort.ascending() : sort.descending();

    PageRequest pageable = PageRequest.of(pageNum - 1, pageSize, sort);
    Page<?> page = null;

    if (keyword != null) {
      page = repo.findAll(keyword, pageable);
    }else{
      page = repo.findAll(pageable);
    }

    updateModelAttributes(pageNum, page);

  }



}
