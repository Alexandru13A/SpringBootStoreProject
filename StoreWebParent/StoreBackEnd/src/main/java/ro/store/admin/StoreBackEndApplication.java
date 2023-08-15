package ro.store.admin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@SpringBootApplication
@EntityScan({ "ro.store.common.entity", "ro.store.admin.user" })
public class StoreBackEndApplication {

	public static void main(String[] args) {
		SpringApplication.run(StoreBackEndApplication.class, args);
	}

}
