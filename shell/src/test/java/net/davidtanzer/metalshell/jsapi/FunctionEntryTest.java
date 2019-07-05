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

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.util.List;
import java.util.function.Consumer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class FunctionEntryTest {
	@Test
	void buildsDescriptionWithType() {
		FunctionEntry.Builder builder = new FunctionEntry.Builder();
		FunctionEntry entry = builder.build();

		StringBuilder descBuilder = new StringBuilder();
		entry.createDescription(descBuilder);

		assertThat(descBuilder.toString()).isEqualTo("{\"type\":\"function\"}");
	}

	@Test
	void setsReturnTypeToArrayWhenInstanceOfJsArray() throws NoSuchMethodException {
		FunctionEntry.Builder builder = new FunctionEntry.Builder();
		Method functionReturningArray = ApiObject.class.getMethod("stringArray");
		builder.setProxiedFunction(functionReturningArray);
		FunctionEntry entry = builder.build();

		StringBuilder descBuilder = new StringBuilder();
		entry.createDescription(descBuilder);

		assertThat(descBuilder.toString()).isEqualTo("{\"type\":\"function\",\"returns\":\"net.davidtanzer.metalshell.jsapi.FunctionEntryTest$StringArray\"}");
	}

	@Test
	void addsReturnTypeToParent() throws NoSuchMethodException {
		Consumer<Class<?>> consumer = mock(Consumer.class);
		FunctionEntry.Builder builder = new FunctionEntry.Builder(consumer);
		Method functionReturningArray = ApiObject.class.getMethod("stringArray");
		builder.setProxiedFunction(functionReturningArray);
		FunctionEntry entry = builder.build();

		StringBuilder descBuilder = new StringBuilder();
		entry.createDescription(descBuilder);

		verify(consumer).accept(StringArray.class);
	}

	@Test
	void callCallsFunctionWithNoArguments() throws NoSuchMethodException {
		ApiObject apiObject = mock(ApiObject.class);
		Method runMethod = apiObject.getClass().getMethod("noArguments");
		FunctionEntry.Builder builder = new FunctionEntry.Builder();
		builder.setProxiedFunction(runMethod);
		FunctionEntry entry = builder.build();

		entry.call(apiObject, new Object[0]);

		verify(apiObject).noArguments();
	}

	@Test
	void callCallsFunctionWhereAllArgumentsMatch() throws NoSuchMethodException {
		ApiObject apiObject = mock(ApiObject.class);
		Method runMethod = apiObject.getClass().getMethod("argumentsMatch", new Class<?>[] { String.class, Integer.class });
		FunctionEntry.Builder builder = new FunctionEntry.Builder();
		builder.setProxiedFunction(runMethod);
		FunctionEntry entry = builder.build();

		entry.call(apiObject, new Object[] { "the string", 1234});

		verify(apiObject).argumentsMatch("the string", 1234);
	}

	@Test
	void callCallsFunctionWhereSimpleArgumentConversionsAreNecessary() throws NoSuchMethodException {
		ApiObject apiObject = mock(ApiObject.class);
		Method runMethod = apiObject.getClass().getMethod("simpleArgumentConversionsNecessary", new Class<?>[] { String.class, Integer.class, int.class });
		FunctionEntry.Builder builder = new FunctionEntry.Builder();
		builder.setProxiedFunction(runMethod);
		FunctionEntry entry = builder.build();

		entry.call(apiObject, new Object[] { "the string", 12.7, 13.2});

		verify(apiObject).simpleArgumentConversionsNecessary("the string", 12, 13);
	}

	@Test
	void callReturnsValueReturnedByProxiedFunction() throws NoSuchMethodException {
		ApiObject apiObject = mock(ApiObject.class);
		StringArray expectedReturnValue = mock(StringArray.class);
		when(apiObject.stringArray()).thenReturn(expectedReturnValue);
		Method runMethod = apiObject.getClass().getMethod("stringArray");
		FunctionEntry.Builder builder = new FunctionEntry.Builder();
		builder.setProxiedFunction(runMethod);
		FunctionEntry entry = builder.build();

		Object returnValue = entry.call(apiObject, new Object[0]);

		assertThat(returnValue).isSameAs(expectedReturnValue);
	}

	@Test @Disabled
	void todoAlsoTestNullValues() {

	}

	private class ApiObject {
		public void noArguments() {
		}

		public void argumentsMatch(String str, Integer x) {
		}

		public void simpleArgumentConversionsNecessary(String str, Integer x, int y) {
		}

		public StringArray stringArray() {
			return null;
		}
	}

	private class StringArray extends JsArray<String> {
		public StringArray(List<String> items) {
			super(items);
		}
	}
}