package net.davidtanzer.metalshell.jsapi;

import java.lang.reflect.Type;

public class ArrayEntry extends ApiEntry {
	private Class<?> elementType;

	private ArrayEntry() {}

	public Class<?> getElementType() {
		return elementType;
	}

	@Override
	protected void createDescription(StringBuilder builder) {
		builder.append("{");
		builder.append("\"--type\":\"array\",\"--elementType\":\"").append(elementType.getName()).append("\"");
		builder.append("}");
	}

	static class Builder extends ApiEntry.Builder<ArrayEntry> {
		Builder() {
			super(new ArrayEntry());
		}

		public void setElementType(Class elementType) {
			building.elementType = elementType;
		}
	}

}
