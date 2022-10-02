package net.catenax.autosetup.utility;

import org.apache.commons.text.StringEscapeUtils;

public class LogUtil {
	
	public static String encode(String message) {
		return StringEscapeUtils.unescapeHtml4(StringEscapeUtils.escapeJava(message));
	}

}
