package ro.store.admin.order;

import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import ro.store.admin.common.paging.PagingAndSortingHelper;
import ro.store.admin.countries_states_backend.CountryRepository;
import ro.store.common.entity.Country;
import ro.store.common.entity.order.Order;
import ro.store.common.entity.order.OrderStatus;
import ro.store.common.entity.order.OrderTrack;

@Service
public class OrderService {

  private static final int ORDERS_PER_PAGE = 10;
  private OrderRepository orderRepository;
  private CountryRepository countryRepository;

  public OrderService(OrderRepository orderRepository,CountryRepository countryRepository) {
    this.orderRepository = orderRepository;
    this.countryRepository = countryRepository;
  }

  public void listByPage(int pageNum, PagingAndSortingHelper helper) {
    String sortField = helper.getSortField();
    String sortOrder = helper.getSortDir();
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

  public List<Country> listAllCountries(){
    return countryRepository.findAllByOrderByNameAsc();
  }

  public void save(Order orderInForm) {
		Order orderInDB = orderRepository.findById(orderInForm.getId()).get();
		orderInForm.setOrderTime(orderInDB.getOrderTime());
		orderInForm.setCustomer(orderInDB.getCustomer());
		
		orderRepository.save(orderInForm);
	}	
	
	public void updateStatus(Integer orderId, String status) {
		Order orderInDB = orderRepository.findById(orderId).get();
		OrderStatus statusToUpdate = OrderStatus.valueOf(status);
		
		if (!orderInDB.hasStatus(statusToUpdate)) {
			List<OrderTrack> orderTracks = orderInDB.getOrderTracks();
			
			OrderTrack track = new OrderTrack();
			track.setOrder(orderInDB);
			track.setStatus(statusToUpdate);
			track.setUpdatedTime(new Date());
			track.setNotes(statusToUpdate.defaultDescription());
			
			orderTracks.add(track);
			
			orderInDB.setOrderStatus(statusToUpdate);
			
			orderRepository.save(orderInDB);
		}
		
	}


}
