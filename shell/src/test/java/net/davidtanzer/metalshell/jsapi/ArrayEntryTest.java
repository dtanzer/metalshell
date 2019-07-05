package net.davidtanzer.metalshell.jsapi;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class ArrayEntryTest {
	@Test
	void addsAllClassMethodsInAnObject() {
		ArrayEntry.Builder builder = new ArrayEntry.Builder();
		builder.setElementType(String.class);
		ArrayEntry entry = builder.build();

		StringBuilder descBuilder = new StringBuilder();
		entry.createDescription(descBuilder);

		assertThat(descBuilder.toString()).isEqualTo("{\"--type\":\"array\",\"--elementType\":\"java.lang.String\"}");
	}

}