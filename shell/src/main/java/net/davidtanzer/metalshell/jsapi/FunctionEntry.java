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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.function.Consumer;

class FunctionEntry extends ApiEntry {
	private Method proxiedFunction;

	private FunctionEntry() {
	}

	@Override
	protected void createDescription(StringBuilder builder) {
		builder.append("{\"type\":\"function\"");
		if(proxiedFunction != null) {
			builder.append(",\"returns\":\"")
					.append(proxiedFunction.getReturnType().getName())
					.append("\"");
		}
		builder.append("}");
	}

	public Object call(Object self, Object[] args) {

		try {
			Object[] callArgs = new Object[args.length];
			Class<?>[] argTypes = proxiedFunction.getParameterTypes();

			for(int i=0; i<args.length; i++) {
				if(args[i].getClass() == argTypes[i]) {
					callArgs[i] = args[i];
				} else {
					callArgs[i] = convertArg(args[i], argTypes[i]);
				}
			}

			return proxiedFunction.invoke(self, callArgs);
		} catch (IllegalAccessException | InvocationTargetException e) {
			throw new IllegalStateException("Could not invoke API method "+proxiedFunction+" on object "+self, e);
		}
	}

	private Object convertArg(Object arg, Class<?> argType) {
		if((argType == Integer.class || argType == int.class) && arg.getClass() == Double.class) {
			return (int) ((Double)arg).doubleValue();
		}
		throw new IllegalStateException("Could not convert "+arg+" to "+argType);
	}

	public Method getProxiedFunction() {
		return proxiedFunction;
	}

	static class Builder extends ApiEntry.Builder<FunctionEntry> {
		private final Consumer<Class<?>> appendClass;

		Builder() {
			this(c -> {});
		}

		Builder(Consumer<Class<?>> appendClass) {
			super(new FunctionEntry());
			this.appendClass = appendClass;
		}

		public void setProxiedFunction(Method function) {
			building.proxiedFunction = function;
			appendClass.accept(function.getReturnType());
		}
	}

}
