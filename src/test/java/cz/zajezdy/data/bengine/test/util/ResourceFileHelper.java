package cz.zajezdy.data.bengine.test.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;

import org.apache.commons.io.IOUtils;


public class ResourceFileHelper {

	public static String getFileContent(String resourceFileName) throws IOException {
		ClassLoader classloader = Thread.currentThread().getContextClassLoader();
		InputStream is = classloader.getResourceAsStream(resourceFileName);
		StringWriter writer = new StringWriter();
		IOUtils.copy(is, writer, "UTF-8");
		return writer.toString();
	}

	public static String getFileContentNoException(String resourceFileName) {
		ClassLoader classloader = Thread.currentThread().getContextClassLoader();
		InputStream is = classloader.getResourceAsStream(resourceFileName);
		StringWriter writer = new StringWriter();
		try {
			IOUtils.copy(is, writer, "UTF-8");
		}
		catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException("IOException occured: " + e.getMessage());
		}
		return writer.toString();
	}
}
