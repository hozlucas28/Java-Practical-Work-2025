package utilities;

/**
 * Utility class for transform strings.
 */
public class StringTransformers {
	/**
	 * Converts the string to title case. Each word in the string will have its
	 * first character in uppercase and the rest in lowercase. Words are separated
	 * by whitespace.
	 *
	 * @param str string to be transformed
	 * @return title-cased string, or the original string if it is null or empty
	 */
	public static String toTitle(String str) {
		if (str == null || str.isEmpty()) {
			return str;
		}

		String[] words = str.split("\\s+");
		StringBuilder builder = new StringBuilder();

		for (String word : words) {
			if (word.length() > 1) {
				builder.append(word.substring(0, 1).toUpperCase());
				builder.append(word.substring(1).toLowerCase());
				builder.append(" ");
			}
		}

		return builder.toString().trim();
	}

	/**
	 * Capitalizes the first character of the string and converts the rest to
	 * lowercase.
	 *
	 * @param str string to be capitalized
	 * @return capitalized string, or the original string if it is null or empty
	 */
	public static String toCapitalize(String str) {
		if (str == null || str.isEmpty()) {
			return str;
		}

		return str.substring(0, 1).toUpperCase() + str.substring(1).toLowerCase();
	}
}
