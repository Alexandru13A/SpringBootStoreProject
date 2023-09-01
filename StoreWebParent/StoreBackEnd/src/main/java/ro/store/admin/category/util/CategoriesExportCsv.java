package ro.store.admin.category.util;

import java.io.IOException;
import java.util.List;

import org.supercsv.io.CsvBeanWriter;
import org.supercsv.io.ICsvBeanWriter;
import org.supercsv.prefs.CsvPreference;

import jakarta.servlet.http.HttpServletResponse;
import ro.store.admin.common.util.CommonExportFunction;
import ro.store.common.entity.Category;

public class CategoriesExportCsv extends CommonExportFunction {

  public void export(List<Category> categories, HttpServletResponse response) throws IOException {

    super.setResponseHeader(response, "text/csv", ".csv", "categories_");

    ICsvBeanWriter csvWriter = new CsvBeanWriter(response.getWriter(),CsvPreference.STANDARD_PREFERENCE);

    String[] csvHeader = { "Category ID", "Category Name" };
    String[] fieldMapping = { "id", "name" };

    csvWriter.writeHeader(csvHeader);

    for (Category category : categories) {
      category.setName(category.getName().replace("--", "   "));
      csvWriter.write(category, fieldMapping);
    }

    csvWriter.close();
  }
}