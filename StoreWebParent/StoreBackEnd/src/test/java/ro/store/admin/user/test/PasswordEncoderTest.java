package ro.store.admin.user.test;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordEncoderTest {

	@Test
	public void testEncodePassword() {
		BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
		String rawPassword = "alex12345";
		String encodedPassword = passwordEncoder.encode(rawPassword);

		System.out.println(encodedPassword);

		boolean matches = passwordEncoder.matches(rawPassword, encodedPassword);
		assertThat(matches).isTrue();
	}
}
