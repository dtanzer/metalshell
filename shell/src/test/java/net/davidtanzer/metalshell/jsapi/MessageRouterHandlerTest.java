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

import org.cef.callback.CefQueryCallback;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.*;

class MessageRouterHandlerTest {
	@Test
	void rebuildsApisJSCacheWhenApisChanged() {
		Supplier<Map<String, Object>> supplier = () -> new HashMap<>();
		Function<Map<String, Object>, JsApiCache> jsCacheBuilder = mock(Function.class);
		MessageRouterHandler handler = new MessageRouterHandler(supplier, jsCacheBuilder);

		handler.onQuery(null,null,0,"request",false, mock(CefQueryCallback.class));
		handler.onQuery(null,null,0,"request",false, mock(CefQueryCallback.class));

		verify(jsCacheBuilder, times(2)).apply(any());
	}

	@Test
	void doesNotRebuildApisJSCacheWhenApisUnchanged() {
		Map<String, Object> map = new HashMap<>();
		Supplier<Map<String, Object>> supplier = () -> map;
		Function<Map<String, Object>, JsApiCache> jsCacheBuilder = mock(Function.class);
		when(jsCacheBuilder.apply(any())).thenReturn(mock(JsApiCache.class));
		MessageRouterHandler handler = new MessageRouterHandler(supplier, jsCacheBuilder);

		handler.onQuery(null,null,0,"request",false, mock(CefQueryCallback.class));
		handler.onQuery(null,null,0,"request",false, mock(CefQueryCallback.class));

		verify(jsCacheBuilder, times(1)).apply(map);
	}

	@Test
	void returnsApisDescriptionToJsWhenRequestIsGetApis() {
		Supplier<Map<String, Object>> supplier = () -> new HashMap<>();
		Function<Map<String, Object>, JsApiCache> jsCacheBuilder = mock(Function.class);
		JsApiCache jsCache = mock(JsApiCache.class);
		when(jsCache.getApiDescription()).thenReturn("the description");
		when(jsCacheBuilder.apply(any())).thenReturn(jsCache);
		MessageRouterHandler handler = new MessageRouterHandler(supplier, jsCacheBuilder);

		CefQueryCallback callback = mock(CefQueryCallback.class);
		handler.onQuery(null,null,0,"--getApis",false, callback);

		verify(callback).success("the description");
	}
}