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

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Map;

public class JsCacheBuilder {
	public JsApiCache buildFrom(Map<String, Object> registeredApis) {
		JsApiCache.Builder builder = new JsApiCache.Builder();

		for(String apiBaseName : registeredApis.keySet()) {
			appendObject(builder, apiBaseName, registeredApis.get(apiBaseName));
		}

		return builder.build();
	}

	private void appendObject(JsApiCache.Builder apiBuilder, String apiBaseName, Object apiObject) {
		Class<?> objClass = apiObject.getClass();
		ObjectEntry.Builder objBuilder = new ObjectEntry.Builder();

		ClassEntry classEntry = appendType(apiBuilder, objClass);
		objBuilder.setClass(objClass.getName(), classEntry);
		objBuilder.setProxiedObject(apiObject);

		apiBuilder.add(apiBaseName, objBuilder.build());
	}

	private ClassEntry appendType(JsApiCache.Builder apiBuilder, Class<?> objClass) {
		ClassEntry.Builder classBuilder = new ClassEntry.Builder();

		Method[] methods = objClass.getMethods();
		for(Method m : methods) {
			if(!isMethodOfClassObject(m)) {
				appendFunction(classBuilder, m, apiBuilder);
			}
		}

		ClassEntry classEntry = classBuilder.build();
		apiBuilder.addClass(objClass, classEntry);
		return classEntry;
	}

	private void appendFunction(ClassEntry.Builder classBuilder, Method m, JsApiCache.Builder apiBuilder) {
		FunctionEntry.Builder builder = new FunctionEntry.Builder(c -> appendClassUsedInFunction(c, apiBuilder));
		builder.setProxiedFunction(m);
		classBuilder.add(m.getName(), builder.build());
	}

	private void appendClassUsedInFunction(Class c, JsApiCache.Builder apiBuilder) {
		if(JsArray.class.isAssignableFrom(c)) {
			appendArrayType(c, apiBuilder);
		}
		//TODO append class that is not an array
	}

	private void appendArrayType(Class c, JsApiCache.Builder apiBuilder) {
		ArrayEntry.Builder builder = new ArrayEntry.Builder();

		Type actualTypeArgument = ((ParameterizedType) c.getGenericSuperclass()).getActualTypeArguments()[0];
		try {
			Class<?> elementType = Class.forName(actualTypeArgument.getTypeName());
			builder.setElementType(elementType);

			appendType(apiBuilder, elementType);
		} catch (ClassNotFoundException e) {
			throw new IllegalStateException("Could not find class for array type: "+actualTypeArgument.getTypeName());
		}

		apiBuilder.addArrayType(c, builder.build());
	}

	private boolean isMethodOfClassObject(Method m) {
		try {
			Object.class.getMethod(m.getName(), m.getParameterTypes());
		} catch (NoSuchMethodException e) {
			return false;
		}
		return true;
	}
}
