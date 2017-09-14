package cz.zajezdy.data.bengine.test.util;

import java.util.HashMap;
import java.util.Map;


public class InputHelper {

	public static Map<String, Object> getTestDocumentInput() {
		Map<String, Object> input = new HashMap<String, Object>();
		input.put("textInput", new String("test1"));
		input.put("input2", "test1");
		input.put("inputNumber", new Double(999.99));
		// input.put("optionalNumber", new Integer(123));
		input.put("sphere", new Double(5.0));
		input.put("changingString", "hello");
		return input;
	}

	public static Map<String, String> getTestDocumentInputStrings() {
		Map<String, String> input = new HashMap<String, String>();
		input.put("textInput", new String("test1"));
		input.put("input2", "test1");
		input.put("inputNumber", "999.99");
		// input.put("optionalNumber", new Integer(123));
		input.put("sphere", "5.0");
		input.put("changingString", "hello");
		return input;
	}

	public static Map<String, Object> getComputerConfigurationInput() {
		Map<String, Object> input = new HashMap<String, Object>();
		input.put("ghzOption", new Double(1.5));
		input.put("country", "de");
		input.put("category", "desktop");
		return input;
	}

}
