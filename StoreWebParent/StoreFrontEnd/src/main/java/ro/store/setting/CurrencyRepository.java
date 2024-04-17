package ro.store.setting;

import org.springframework.data.repository.CrudRepository;

import ro.store.common.entity.Currency;

public interface CurrencyRepository extends CrudRepository<Currency,Integer> {

  
  
}
