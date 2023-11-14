package ro.store.admin.order;

import java.util.NoSuchElementException;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import ro.store.admin.common.paging.PagingAndSortingHelper;
import ro.store.common.entity.order.Order;

@Service
public class OrderService {

  private static final int ORDERS_PER_PAGE = 10;
  private OrderRepository orderRepository;

  public OrderService(OrderRepository orderRepository) {
    this.orderRepository = orderRepository;
  }

  public void listByPage(int pageNum, PagingAndSortingHelper helper) {
    String sortField = helper.getSortField();
    String sortOrder = helper.getSortOrder();
    String keyword = helper.getKeyword();

    Sort sort = null;

    if ("destination".equals(sortField)) {
      sort = Sort.by("country").and(Sort.by("state")).and(Sort.by("city"));
    } else {
      sort = Sort.by(sortField);
    }

    sort = sortOrder.equals("asc") ? sort.ascending() : sort.descending();
    PageRequest pageable = PageRequest.of(pageNum - 1, ORDERS_PER_PAGE, sort);

    Page<Order> page = null;

    if (keyword != null) {
      page = orderRepository.findAll(keyword, pageable);
    } else {
      page = orderRepository.findAll(pageable);
    }

    helper.updateModelAttributes(pageNum, page);

  }

  public Order getOrderById(Integer id) throws OrderNotFoundException {
    try {
      return orderRepository.findById(id).get();
    } catch (NoSuchElementException e) {
      throw new OrderNotFoundException("Could not found any order with ID: " + id);
    }
  }

  public void deleteOrderById(Integer id) throws OrderNotFoundException {

    Long count = orderRepository.countById(id);

    if (count == null || count == 0) {
      throw new OrderNotFoundException("Could not find any Order with ID: " + id);
    }
    orderRepository.deleteById(id);

  }

}
