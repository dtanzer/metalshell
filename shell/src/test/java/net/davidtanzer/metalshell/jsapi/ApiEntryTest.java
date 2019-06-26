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

class ApiEntryTest {
	@Test
	void printDescriptionPrintsSingleEntry() {
		ApiEntry inner1 = new ApiEntry() {
			@Override
			protected void createDescription(StringBuilder builder) {
				builder.append("desc");
			}
		};
		TestApiEntry apiEntry = new TestApiEntry(inner1);

		StringBuilder builder = new StringBuilder();
		apiEntry.createDescription(builder);
		assertThat(builder.toString()).isEqualTo("\"0\":desc");
	}

	@Test
	void printDescriptionPrintsEntriesSeparatedByComma() {
		ApiEntry inner1 = new ApiEntry() {
			@Override
			protected void createDescription(StringBuilder builder) {
				builder.append("desc");
			}
		};
		TestApiEntry apiEntry = new TestApiEntry(inner1, inner1);

		StringBuilder builder = new StringBuilder();
		apiEntry.createDescription(builder);
		assertThat(builder.toString()).matches("\"\\d\":desc,\"\\d\":desc");
	}

	private class TestApiEntry extends ApiEntry {
		public TestApiEntry(ApiEntry... innerEntries) {
			for (int i = 0; i < innerEntries.length; i++) {
				this.entries.put(String.valueOf(i), innerEntries[i]);
			}
		}
	}
}