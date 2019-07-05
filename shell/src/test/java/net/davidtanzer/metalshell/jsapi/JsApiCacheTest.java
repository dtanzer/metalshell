/*  MetalShell - Create HTML+JS user interfaces for JVM applications
    Copyright (C) 2019  David Tanzer

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Affero General Public License for more details.

    You should have received a copy of the GNU Affero General Public License
    along with this program.  If not, see <https://www.gnu.org/licenses/>.
*/
package net.davidtanzer.metalshell.jsapi;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;
import static org.mockito.Mockito.*;

class JsApiCacheTest {
	private ObjectDescription.Builder odBuilder;
	private ObjectDescription.ArrayDescriptionBuilder adBuilder;
	private ObjectDescription arrayObjectDescription;
	private ObjectDescription emptyDescription;
	private ObjectEntry.Builder entryBuilder;
	private ObjectDescription.ObjectDescriptionBuilder objectDescBuilder;
	private ObjectDescription objectDescription;

	@BeforeEach
	public void setup() {
		odBuilder = mock(ObjectDescription.Builder.class);

		adBuilder = mock(ObjectDescription.ArrayDescriptionBuilder.class);
		when(odBuilder.setArray(any(), any(), any())).thenReturn(adBuilder);
		arrayObjectDescription = mock(ObjectDescription.class);
		when(adBuilder.build()).thenReturn(arrayObjectDescription);

		emptyDescription = mock(ObjectDescription.class);
		when(odBuilder.empty()).thenReturn(emptyDescription);

		objectDescBuilder = mock(ObjectDescription.ObjectDescriptionBuilder.class);
		objectDescription = mock(ObjectDescription.class);
		when(objectDescBuilder.build()).thenReturn(objectDescription);
		when(odBuilder.setObject(any(), any())).thenReturn(objectDescBuilder);

		entryBuilder = mock(ObjectEntry.Builder.class);
	}
	@Test
	void createsDescriptionForAllApiEntries() {
		JsApiCache.Builder builder = new JsApiCache.Builder();
		builder.add("foo", new ApiEntry() {
			@Override
			protected void createDescription(StringBuilder builder) {
				builder.append("desc");
			}
		});
		JsApiCache cache = builder.build();

		String description = cache.getApiDescription();

		assertThat(description).isEqualTo("{\"foo\":desc}");
	}

	@Test
	void callPassesOnTheFunctionCallToTheCorrectObject() {
		JsApiCache.Builder builder = new JsApiCache.Builder(() -> odBuilder, () -> entryBuilder);
		ObjectEntry objectEntry = mock(ObjectEntry.class);
		builder.add("foo", objectEntry);
		JsApiCache cache = builder.build();

		Object[] args = new Object[] {"x", 5};
		FunctionCall functionCall = new FunctionCall("foo", "x", args);
		cache.call(functionCall);

		verify(objectEntry).call("x", args);
	}

	@Test
	void callReturnsObjectDescriptionFromResult() {
		JsApiCache.Builder builder = new JsApiCache.Builder(() -> odBuilder, () -> entryBuilder);
		ObjectEntry objectEntry = mock(ObjectEntry.class);
		when(objectEntry.call(any(), any())).thenReturn(new FooArray(Collections.emptyList()));
		builder.add("foo", objectEntry);
		JsApiCache cache = builder.build();

		Object[] args = new Object[] {"x", 5};
		FunctionCall functionCall = new FunctionCall("foo", "x", args);
		ObjectDescription result = cache.call(functionCall);

		assertThat(result).isSameAs(arrayObjectDescription);
	}

	@Test
	void addsArrayItemsToTheOdBuilderWhenReturnTypeIsArray() {
		JsApiCache.Builder builder = new JsApiCache.Builder(() -> odBuilder, () -> entryBuilder);
		builder.addClass(Foo.class, mock(ClassEntry.class));
		ObjectEntry objectEntry = mock(ObjectEntry.class);
		Foo foo1 = mock(Foo.class);
		Foo foo2 = mock(Foo.class);
		FooArray array = new FooArray(List.of(foo1, foo2));
		when(objectEntry.call(any(), any())).thenReturn(array);
		builder.add("foo", objectEntry);
		JsApiCache cache = builder.build();

		Object[] args = new Object[] {"x", 5};
		FunctionCall functionCall = new FunctionCall("foo", "x", args);
		ObjectDescription result = cache.call(functionCall);

		verify(odBuilder).setArray(any(),eq(array), eq(Foo.class));
		verify(adBuilder).addItem(any(), eq(foo1));
		verify(adBuilder).addItem(any(), eq(foo2));
	}

	@Test
	void returnsEmptyBuilderWhenReturnTypeIsNull() {
		JsApiCache.Builder builder = new JsApiCache.Builder(() -> odBuilder, () -> entryBuilder);
		ObjectEntry objectEntry = mock(ObjectEntry.class);
		when(objectEntry.call(any(), any())).thenReturn(null);
		builder.add("foo", objectEntry);
		JsApiCache cache = builder.build();

		Object[] args = new Object[] {"x", 5};
		FunctionCall functionCall = new FunctionCall("foo", "x", args);
		ObjectDescription result = cache.call(functionCall);

		assertThat(result).isSameAs(emptyDescription);
	}

	@Test
	void canCallFunctionBasedOnObjectId() {
		JsApiCache.Builder builder = new JsApiCache.Builder(() -> odBuilder, () -> entryBuilder);
		builder.addClass(Foo.class, mock(ClassEntry.class));
		ObjectEntry objectEntry = mock(ObjectEntry.class);
		Foo foo = mock(Foo.class);
		FooArray array = new FooArray(List.of(foo));
		when(objectEntry.call(any(), any())).thenReturn(array);
		builder.add("foo", objectEntry);

		ObjectEntry elementObjectEntry = mock(ObjectEntry.class);
		when(entryBuilder.build()).thenReturn(elementObjectEntry);
		JsApiCache cache = builder.build();

		Object[] args = new Object[] {"x", 5};
		FunctionCall functionCall = new FunctionCall("foo", "x", args);
		cache.call(functionCall);

		ArgumentCaptor<CacheKey> cacheKey = ArgumentCaptor.forClass(CacheKey.class);
		verify(adBuilder).addItem(cacheKey.capture(), eq(foo));

		FunctionCall functionCall2 = new FunctionCall(cacheKey.getValue().asString(), "y", args);
		cache.call(functionCall2);
		verify(elementObjectEntry).call("y", args);
	}

	@Test
	void returnsObjectIfTypeIsNotArray() {
		JsApiCache.Builder builder = new JsApiCache.Builder(() -> odBuilder, () -> entryBuilder);
		ObjectEntry objectEntry = mock(ObjectEntry.class);
		when(objectEntry.call(any(), any())).thenReturn("some string");
		builder.add("foo", objectEntry);
		JsApiCache cache = builder.build();

		FunctionCall functionCall = new FunctionCall("foo", "x", new Object[0]);
		ObjectDescription result = cache.call(functionCall);

		assertThat(result).isSameAs(objectDescription);
	}

	private class FooArray extends JsArray<Foo> {
		public FooArray(List<Foo> items) {
			super(items);
		}
	}

	private class Foo {
	}
}