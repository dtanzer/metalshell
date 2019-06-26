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

import java.util.HashMap;
import java.util.Map;

public abstract class ApiEntry {
	protected final Map<String, ApiEntry> entries = new HashMap<>();

	ApiEntry get(String name) {
		return entries.get(name);
	}

	protected void createDescription(StringBuilder builder) {
		boolean prependComma = false;
		for(String name : entries.keySet()) {
			if(prependComma) {
				builder.append(",");
			}
			builder.append("\"").append(name).append("\":");
			entries.get(name).createDescription(builder);
			prependComma = true;
		}
	}

	static abstract class Builder<T extends ApiEntry> {
		protected T building;

		public Builder(T building) {
			this.building = building;
		}

		T build() {
			T result = building;
			building = null;
			return result;
		}
	}
}
