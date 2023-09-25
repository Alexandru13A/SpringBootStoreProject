package ro.store.admin.currency;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import ro.store.common.entity.Currency;

public interface CurrencyRepository extends JpaRepository<Currency, Integer> {

  
  public List<Currency> findAllByOrderByNameAsc();

}
