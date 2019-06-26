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

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class ObjectEntryTest {
	@Test
	void buildsDescriptionWithTypeAndClass() {
		ObjectEntry.Builder builder = new ObjectEntry.Builder();
		builder.setClass("com.example.SomeClass", null);
		ObjectEntry entry = builder.build();

		StringBuilder descBuilder = new StringBuilder();
		entry.createDescription(descBuilder);

		assertThat(descBuilder.toString()).isEqualTo("{\"type\":\"object\",\"class\":\"com.example.SomeClass\"}");
	}

	@Test
	void passesOnFunctionCallToTheCorrectFunctionEntry() {
		Object proxiedObject = new Object();
		FunctionEntry functionEntry = mock(FunctionEntry.class);
		ClassEntry.Builder classEntryBuilder = new ClassEntry.Builder();
		classEntryBuilder.add("function1", functionEntry);
		ClassEntry classEntry = classEntryBuilder.build();

		ObjectEntry.Builder builder = new ObjectEntry.Builder();
		builder.setClass("ignore", classEntry);
		builder.setProxiedObject(proxiedObject);
		ObjectEntry objectEntry = builder.build();

		Object[] args = new Object[] {"one", 2, true};
		objectEntry.call("function1", args);

		verify(functionEntry).call(proxiedObject, args);
	}
}