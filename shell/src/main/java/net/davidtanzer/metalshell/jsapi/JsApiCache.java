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

import java.lang.reflect.ParameterizedType;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;

//FIXME setup for tests too complex, refactor!
public class JsApiCache extends ApiEntry{
	private Supplier<ObjectEntry.Builder> entryBuilderSupplier;
	private Supplier<ObjectDescription.Builder> odBuilderSupplier;
	//FIXME make local to an api call session!
	private HashMap<CacheKey, ObjectEntry> sessionEntries = new HashMap<>();

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

	ObjectDescription call(FunctionCall functionCall) {
		//Check for errors...
		ObjectEntry objectEntry = (ObjectEntry) this.entries.get(functionCall.getObject());
		if(objectEntry == null) {
			objectEntry = this.sessionEntries.get(SessionCacheKey.of(functionCall.getObject()));
		}

		Object result = objectEntry.call(functionCall.getFunction(), functionCall.getArgs());
		if(result instanceof JsArray<?>) {
			Class<?> elementType = null;
			try {
				ParameterizedType genericSuperclass = (ParameterizedType) result.getClass().getGenericSuperclass();
				String typeName = genericSuperclass.getActualTypeArguments()[0].getTypeName();
				elementType = Class.forName(typeName);
			} catch (ClassNotFoundException e) {
				throw new IllegalStateException("Could not get element type of JsArray", e);
			}
			JsArray<?> jsArray = (JsArray<?>) result;
			ObjectDescription.ArrayDescriptionBuilder arrayDescriptionBuilder = odBuilderSupplier.get()
					.setArray(new SessionCacheKey(), jsArray, elementType);

			for(Object item : jsArray.getItems()) {
				SessionCacheKey cacheKey = new SessionCacheKey();

				ObjectEntry.Builder oeBuilder = entryBuilderSupplier.get();
				oeBuilder.setProxiedObject(item);
				oeBuilder.setClass(item.getClass().getName(), (ClassEntry) this.entries.get("--classes").get(item.getClass().getName()));
				sessionEntries.put(cacheKey, oeBuilder.build());

				arrayDescriptionBuilder.addItem(cacheKey, item);
			}

			return arrayDescriptionBuilder
					.build();
		} else if(result == null) {
			return odBuilderSupplier.get().empty();
		}

		return odBuilderSupplier.get().setObject(null, result).build();
	}

	static class Builder extends ApiEntry.Builder<JsApiCache> {
		private Set<Class<?>> classes = new HashSet<>();

		public Builder() {
			this(ObjectDescription.Builder::new, ObjectEntry.Builder::new);
		}
		public Builder(Supplier<ObjectDescription.Builder> odBuilderSupplier, Supplier<ObjectEntry.Builder> entryBuilderSupplier) {
			super(new JsApiCache());
			this.building.odBuilderSupplier = odBuilderSupplier;
			this.building.entryBuilderSupplier = entryBuilderSupplier;
		}

		public void add(String apiBaseName, ApiEntry apiEntry) {
			building.entries.put(apiBaseName, apiEntry);
		}

		private boolean hasClass(Class<?> objClass) {
			return classes.contains(objClass);
		}

		public void addClass(Class<?> objClass, ClassEntry classEntry) {
			add(objClass, classEntry, ClassesEntry::addClass);
		}

		public void addArrayType(Class c, ArrayEntry arrayEntry) {
			add(c, arrayEntry, ClassesEntry::addArray);
		}

		private <T> void add(Class c, T entry, Adder<T> adder) {
			if(hasClass(c)) {
				return;
			}
			if(!building.entries.containsKey("--classes")) {
				building.entries.put("--classes", new ClassesEntry());
			}
			classes.add(c);
			adder.addTo(((ClassesEntry)building.entries.get("--classes")), c.getName(), entry);
		}
	}

	private interface Adder<T> {
		void addTo(ClassesEntry classesEntry, String name, T entry);
	}
}
