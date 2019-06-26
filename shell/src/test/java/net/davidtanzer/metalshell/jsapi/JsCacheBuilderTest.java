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

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;
import static org.assertj.core.api.Assumptions.assumeThat;

class JsCacheBuilderTest {
	@Test
	void createsAPropertyEntryForEveryApiObject() {
		JsCacheBuilder cacheBuilder = new JsCacheBuilder();
		Map<String, Object> apis = new HashMap<>();
		apis.put("api1", new Object());

		JsApiCache cache = cacheBuilder.buildFrom(apis);

		assertThat(cache.get("api1")).isInstanceOf(ObjectEntry.class);
	}

	@Test
	void setsProxiedObjectForEveryApiObject() {
		JsCacheBuilder cacheBuilder = new JsCacheBuilder();
		Map<String, Object> apis = new HashMap<>();
		Object proxiedObject = new Object();
		apis.put("api1", proxiedObject);

		JsApiCache cache = cacheBuilder.buildFrom(apis);

		assertThat(((ObjectEntry)cache.get("api1")).getProxiedObject()).isSameAs(proxiedObject);
	}

	@Test
	void setsClassNameOfObjectApiEntry() {
		JsCacheBuilder cacheBuilder = new JsCacheBuilder();
		Map<String, Object> apis = new HashMap<>();
		apis.put("api1", new Object());

		JsApiCache cache = cacheBuilder.buildFrom(apis);

		assumeThat(cache.get("api1")).isInstanceOf(ObjectEntry.class);
		assertThat(((ObjectEntry)cache.get("api1")).getClassName()).isEqualTo("java.lang.Object");
	}

	@Test
	void appendsTypeForEveryObjectClass() {
		JsCacheBuilder cacheBuilder = new JsCacheBuilder();
		Map<String, Object> apis = new HashMap<>();
		apis.put("api1", new ObjectWithFunction());

		JsApiCache cache = cacheBuilder.buildFrom(apis);

		assertThat(cache.get("--classes").get(ObjectWithFunction.class.getName())).isInstanceOf(ClassEntry.class);
	}

	@Test
	void createsAFunctionEntryForEveryFunctionInClass() {
		JsCacheBuilder cacheBuilder = new JsCacheBuilder();
		Map<String, Object> apis = new HashMap<>();
		apis.put("api1", new ObjectWithFunction());

		JsApiCache cache = cacheBuilder.buildFrom(apis);

		assertThat(cache.get("--classes").get(ObjectWithFunction.class.getName()).get("function1")).isInstanceOf(FunctionEntry.class);
	}

	@Test
	void doesNotCreateAFunctionEntryForObjectFunctions() {
		JsCacheBuilder cacheBuilder = new JsCacheBuilder();
		Map<String, Object> apis = new HashMap<>();
		apis.put("api1", new ObjectWithFunction());

		JsApiCache cache = cacheBuilder.buildFrom(apis);

		assertThat(cache.get("--classes").get(ObjectWithFunction.class.getName()).get("equals")).isNull();
		assertThat(cache.get("--classes").get(ObjectWithFunction.class.getName()).get("getClass")).isNull();
		assertThat(cache.get("--classes").get(ObjectWithFunction.class.getName()).get("hashCode")).isNull();
	}

	@Test
	void addsProxiedMethodToFunctionEntry() throws NoSuchMethodException {
		JsCacheBuilder cacheBuilder = new JsCacheBuilder();
		Map<String, Object> apis = new HashMap<>();
		apis.put("api1", new ObjectWithFunction());

		JsApiCache cache = cacheBuilder.buildFrom(apis);

		ApiEntry functionEntry = cache.get("--classes").get(ObjectWithFunction.class.getName()).get("function1");
		assumeThat(functionEntry).isInstanceOf(FunctionEntry.class);
		assertThat(((FunctionEntry)functionEntry).getProxiedFunction()).isEqualTo(ObjectWithFunction.class.getMethod("function1", new Class<?>[0]));
	}

	@Test @Disabled
	void fixmeFindWayToAddOverloadedFunctions() {
		fail("Overloaded functions to not exist in JavaScript, so we can only addess a function by name and" +
				"we'll have to sort it out in Java.");
	}
	private class ObjectWithFunction {
		public void function1() {
		}
	}
}