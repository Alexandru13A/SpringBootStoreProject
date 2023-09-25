package ro.store.admin.user.test.currency;

import static org.assertj.core.api.Assertions.*;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import ro.store.admin.currency.CurrencyRepository;
import ro.store.common.entity.Currency;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Rollback(false)
public class CurrencyRepositoryTest {

  @Autowired
  private CurrencyRepository repository;

  @Test
  public void testCurrency() {

    List<Currency> listCurrencies = Arrays.asList(
        new Currency("United States Dollar", "$", "USD"),
        new Currency("Euro", "€", "EUR"),
        new Currency("Franc", "SFr", "CHF"),
        new Currency("Leu", "-", "RON"),
        new Currency("Lira", "₺", "TRY"),
        new Currency("Lek", "-", "ALL"));

    repository.saveAll(listCurrencies);
    Iterable<Currency> iterable = repository.findAll();
    assertThat(iterable).size().isEqualTo(6);

  }

  @Test
  public void addExtraCurrency() {

    Currency chinese = new Currency("Chinese Yuan", "¥", "CNY");
    Currency japanese = new Currency("Japanese Yuan", "¥", "JPY");
    List<Currency> currencies = Arrays.asList(chinese,japanese);
    repository.saveAll(currencies);
  }

  @Test
  public void testListAllOrderByNameAsc(){
    List<Currency> currencies =repository.findAllByOrderByNameAsc();
    currencies.forEach(System.out :: println);
    assertThat(currencies).size().isGreaterThan(0);
  }

}
