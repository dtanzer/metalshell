package net.davidtanzer.metalshell.jsapi;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class ObjectDescriptionTest {
	@Test
	void createsArrayDescriptionForEmptyArray() {
		ObjectDescription.Builder builder = new ObjectDescription.Builder();

		FooArray array = new FooArray(List.of());
		ObjectDescription.ArrayDescriptionBuilder arrayBuilder = builder.setArray(() -> "cache-key-1", array, Foo.class);

		assertThat(arrayBuilder.build().describe()).isEqualTo(
				"{\"--type\":\"array\",\"--id\":\"cache-key-1\"," +
						"\"--item-type\":\"net.davidtanzer.metalshell.jsapi.ObjectDescriptionTest$Foo\"," +
						"\"items\":[]}" );
	}

	@Test
	void createsArrayDescriptionForArrayContainingItems() {
		ObjectDescription.Builder builder = new ObjectDescription.Builder();

		Foo foo1 = new Foo();
		Foo foo2 = new Foo();
		FooArray array = new FooArray(List.of(foo1, foo2));
		ObjectDescription.ArrayDescriptionBuilder arrayBuilder = builder.setArray(() -> "cache-key-1", array, Foo.class);
		arrayBuilder.addItem(() -> "cache-key-2", foo1);
		arrayBuilder.addItem(() -> "cache-key-3", foo2);

		assertThat(arrayBuilder.build().describe()).contains(
				"\"items\":[" +
						"{\"--id\":\"cache-key-2\",\"--type\":\"net.davidtanzer.metalshell.jsapi.ObjectDescriptionTest$Foo\"}," +
						"{\"--id\":\"cache-key-3\",\"--type\":\"net.davidtanzer.metalshell.jsapi.ObjectDescriptionTest$Foo\"}]" );
	}

	@Test
	void createsObjectDescriptionWithValueForPrimitiveStrings() {
		ObjectDescription.Builder builder = new ObjectDescription.Builder();

		ObjectDescription description = builder.setObject(null, "some string").build();

		assertThat(description.describe()).isEqualTo("{\"--type\":\"PRIMITIVE\",\"value\":\"some string\"}");
	}

	@Test
	void escapesPrimitiveStrings() {
		ObjectDescription.Builder builder = new ObjectDescription.Builder();

		ObjectDescription description = builder.setObject(null, "some \\\\ \n \" string").build();

		assertThat(description.describe()).isEqualTo("{\"--type\":\"PRIMITIVE\",\"value\":\"some \\\\ \\n \\\" string\"}");
	}

	@Test
	void createsObjectDescriptionWithValueForPrimitiveNumbers() {
		ObjectDescription.Builder builder = new ObjectDescription.Builder();

		ObjectDescription description = builder.setObject(null, 5).build();

		assertThat(description.describe()).isEqualTo("{\"--type\":\"PRIMITIVE\",\"value\":5}");
	}

	private class Foo {
	}

	private class FooArray extends JsArray<Foo> {
		public FooArray(List<Foo> items) {
			super(items);
		}
	}
}