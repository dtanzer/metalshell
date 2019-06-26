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

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class JsApiCacheTest {
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
		JsApiCache.Builder builder = new JsApiCache.Builder();
		ObjectEntry objectEntry = mock(ObjectEntry.class);
		builder.add("foo", objectEntry);
		JsApiCache cache = builder.build();

		Object[] args = new Object[] {"x", 5};
		FunctionCall functionCall = new FunctionCall("foo", "x", args);
		cache.call(functionCall);

		verify(objectEntry).call("x", args);
	}
}