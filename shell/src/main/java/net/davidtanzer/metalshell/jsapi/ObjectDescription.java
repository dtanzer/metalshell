package net.davidtanzer.metalshell.jsapi;

import java.awt.Rectangle;
import java.util.*;
import java.util.function.Function;

class ObjectDescription {
	private ObjectDescription() {}

	String describe() {
		return "";
	}

	static class Builder {
		ObjectDescriptionBuilder setObject(CacheKey cacheKey, Object o) {
			return new ObjectDescriptionBuilder(cacheKey, o);
		}

		ArrayDescriptionBuilder setArray(CacheKey cacheKey, JsArray<?> array, Class<?> itemType) {
			return new ArrayDescriptionBuilder(cacheKey, array, itemType);
		}

		public ObjectDescription empty() {
			return new EmptyObjectDescription();
		}
	}


	private static class ArrayObjectDescription extends ObjectDescription {
		public CacheKey cacheKey;
		private Class<?> itemType;
		private List<Item> items = new ArrayList<>();

		@Override
		String describe() {
			StringBuilder builder = new StringBuilder();
			builder.append("{\"--type\":\"array\",\"--id\":\"");
			builder.append(cacheKey.asString());
			builder.append("\",\"--item-type\":\"");
			builder.append(itemType.getName());
			builder.append("\",\"items\":[");

			boolean prependComma = false;
			for(Item item : items) {
				if(prependComma) {
					builder.append(",");
				}

				builder.append("{");
				builder.append("\"--id\":\"").append(item.cacheKey.asString()).append("\",");
				builder.append("\"--type\":\"").append(item.value.getClass().getName()).append("\"");
				builder.append("}");

				prependComma = true;
			}
			builder.append("]}");
			return builder.toString();
		}

		private static class Item {
			private final CacheKey cacheKey;
			private final Object value;

			public Item(CacheKey cacheKey, Object value) {
				this.cacheKey = cacheKey;
				this.value = value;
			}
		}
	}

	static class ArrayDescriptionBuilder {
		private ArrayObjectDescription building;

		private ArrayDescriptionBuilder(CacheKey cacheKey, JsArray<?> array, Class<?> itemType) {
			building = new ArrayObjectDescription();
			building.itemType = itemType;
			building.cacheKey = cacheKey;
		}

		public ObjectDescription build() {
			return building;
		}

		public ArrayDescriptionBuilder addItem(CacheKey cacheKey, Object item) {
			building.items.add(new ArrayObjectDescription.Item(cacheKey, item));
			return this;
		}
	}

	static class ObjectDescriptionBuilder {
		private final CacheKey cacheKey;
		private final Object o;
		private Map<Class<?>, Function<Object, String>> primitiveTypes = new HashMap<Class<?>, Function<Object, String>>() {{
			put(String.class, x -> "\""+escapeString(x)+"\"");
			put(Integer.class, x -> String.valueOf(x));
			put(Boolean.class, x -> String.valueOf(x));
			put(Long.class, x -> String.valueOf(x));
			put(Double.class, x -> String.valueOf(x));
			put(Float.class, x -> String.valueOf(x));
		}};

		private String escapeString(Object x) {
			return String.valueOf(x)
					.replaceAll("\\\\", "\\\\")
					.replaceAll("\n", "\\\\n")
					.replaceAll("\r", "\\\\r")
					.replaceAll("\"", "\\\\\"");
		}

		private ObjectDescriptionBuilder(CacheKey cacheKey, Object o) {
			this.cacheKey = cacheKey;
			this.o = o;
		}

		public ObjectDescription build() {
			if(primitiveTypes.containsKey(o.getClass())) {
				return new PrimitiveDescription(o, primitiveTypes.get(o.getClass()));
			}
			return new ObjectDescription();
		}
	}

	private static class EmptyObjectDescription extends ObjectDescription {
		@Override
		String describe() {
			return "{\"--type\":\"EMPTY\"}";
		}
	}

	private static class PrimitiveDescription extends ObjectDescription {
		private final Object o;
		private final Function<Object, String> formatter;

		public PrimitiveDescription(Object o, Function<Object, String> formatter) {
			this.o = o;
			this.formatter = formatter;
		}

		@Override
		String describe() {
			return "{\"--type\":\"PRIMITIVE\",\"value\":"+formatter.apply(o)+"}";
		}
	}
}
