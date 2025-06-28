package utilities;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class StringTransformersTests {

	@Test
	void toTitle() {
		// Arrange
		String filmName = "hOW tO TrAIn YOuR dRAgoN";

		// Act within assert
		String expected = "How To Train Your Dragon";
		String received = StringTransformers.toTitle(filmName);

		assertEquals(expected, received);
	}

	@Test
	void toCapitalize() {
		// Arrange
		String bookName = "lOReM ipSUm DolOr sit, AMET ConSECtetUR.";

		// Act within assert
		String expected = "Lorem ipsum dolor sit, amet consectetur.";
		String received = StringTransformers.toCapitalize(bookName);

		assertEquals(expected, received);
	}
}
