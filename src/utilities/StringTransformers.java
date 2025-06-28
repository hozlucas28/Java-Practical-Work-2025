package utilities;

public class StringTransformers {
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

	public static String toCapitalize(String str) {
		if (str == null || str.isEmpty()) {
			return str;
		}

		return str.substring(0, 1).toUpperCase() + str.substring(1).toLowerCase();
	}
}
