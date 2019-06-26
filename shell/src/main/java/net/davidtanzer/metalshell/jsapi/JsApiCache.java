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
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class JsApiCache extends ApiEntry{
	private JsApiCache() {
	}

	String getApiDescription() {
		StringBuilder builder = new StringBuilder();
		builder.append("{");
		super.createDescription(builder);
		builder.append("}");
		return builder.toString();
	}

	@Override
	ApiEntry get(String name) {
		return entries.get(name);
	}

	void call(FunctionCall functionCall) {
		//Check for errors...
		ObjectEntry objectEntry = (ObjectEntry) this.entries.get(functionCall.getObject());

		objectEntry.call(functionCall.getFunction(), functionCall.getArgs());
	}

	static class Builder extends ApiEntry.Builder<JsApiCache> {
		private Set<Class<?>> classes = new HashSet<>();

		public Builder() {
			super(new JsApiCache());
		}

		public void add(String apiBaseName, ApiEntry apiEntry) {
			building.entries.put(apiBaseName, apiEntry);
		}

		private boolean hasClass(Class<?> objClass) {
			return classes.contains(objClass);
		}

		public void addClass(Class<?> objClass, ClassEntry classEntry) {
			if(hasClass(objClass)) {
				return;
			}
			if(!building.entries.containsKey("--classes")) {
				building.entries.put("--classes", new ClassesEntry());
			}
			classes.add(objClass);
			((ClassesEntry)building.entries.get("--classes")).addClass(objClass.getName(), classEntry);
		}
	}
}
