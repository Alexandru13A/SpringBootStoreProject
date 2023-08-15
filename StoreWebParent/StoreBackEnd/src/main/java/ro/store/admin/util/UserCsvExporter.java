package ro.store.admin.util;

import java.io.IOException;
import java.util.List;

import org.supercsv.io.CsvBeanWriter;
import org.supercsv.io.ICsvBeanWriter;
import org.supercsv.prefs.CsvPreference;

import jakarta.servlet.http.HttpServletResponse;
import ro.store.common.entity.User;

public class UserCsvExporter extends AbstractExporter {

	public void export(List<User> listUsers, HttpServletResponse response) throws IOException {
		super.setResponseHeader(response, "text/csv", ".csv");

		ICsvBeanWriter csvWriter = new CsvBeanWriter(response.getWriter(), CsvPreference.STANDARD_PREFERENCE);

		String[] csbHeader = { "User ID", "Email", "First Name", "Last Name", "Roles", "Enabled" };
		String[] filedMapping = { "id", "email", "firstName", "lastName", "roles", "enabled" };
		csvWriter.writeHeader(csbHeader);

		for (User user : listUsers) {
			csvWriter.write(user, filedMapping);
		}
		csvWriter.close();
	}

}
