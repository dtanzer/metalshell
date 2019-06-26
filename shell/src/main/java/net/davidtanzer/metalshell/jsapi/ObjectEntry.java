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

class ObjectEntry extends ApiEntry {
	private String className;
	private Object proxiedObject;
	private ClassEntry classEntry;

	private ObjectEntry() {
	}

	@Override
	protected void createDescription(StringBuilder builder) {
		builder.append("{\"type\":\"object\",\"class\":\""+className+"\"}");
	}

	public String getClassName() {
		return className;
	}

	public void call(String functionName, Object[] args) {
		FunctionEntry functionEntry = (FunctionEntry) classEntry.get(functionName);
		functionEntry.call(proxiedObject, args);
	}

	public Object getProxiedObject() {
		return proxiedObject;
	}

	static class Builder extends ApiEntry.Builder<ObjectEntry> {
		Builder() {
			super(new ObjectEntry());
		}

		public void setClass(String className, ClassEntry classEntry) {
			building.className = className;
			building.classEntry = classEntry;
		}

		public void setProxiedObject(Object apiObject) {
			building.proxiedObject = apiObject;
		}
	}
}
