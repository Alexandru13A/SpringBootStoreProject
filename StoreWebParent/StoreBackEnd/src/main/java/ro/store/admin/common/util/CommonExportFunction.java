package ro.store.admin.common.util;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import jakarta.servlet.http.HttpServletResponse;

public class CommonExportFunction {
	
	
	public void setResponseHeader(HttpServletResponse response, String contentType,String extension,String prefix) throws IOException {
		SimpleDateFormat dateFormater = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
		String timestamp = dateFormater.format(new Date());
		String fileName = prefix + timestamp + extension;

		response.setContentType(contentType);

		String headerKey = "Content-Disposition";
		String headerValue = "attachment;filename=" + fileName;
		response.setHeader(headerKey, headerValue);
	}

}
