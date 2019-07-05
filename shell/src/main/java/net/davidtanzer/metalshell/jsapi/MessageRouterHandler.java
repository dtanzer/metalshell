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

import com.google.gson.Gson;
import org.cef.browser.CefBrowser;
import org.cef.browser.CefFrame;
import org.cef.callback.CefQueryCallback;
import org.cef.handler.CefMessageRouterHandlerAdapter;

import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

public class MessageRouterHandler extends CefMessageRouterHandlerAdapter {
	private final Supplier<Map<String, Object>> apisSupplier;
	private final Function<Map<String, Object>, JsApiCache> jsCacheBuilder;
	private Map<String, Object> lastApis;
	private JsApiCache jsApiCache;

	public MessageRouterHandler(Supplier<Map<String, Object>> apisSupplier, Function<Map<String, Object>, JsApiCache> jsCacheBuilder) {
		this.apisSupplier = apisSupplier;
		this.jsCacheBuilder = jsCacheBuilder;
	}

	@Override
	public boolean onQuery(CefBrowser browser, CefFrame frame, long query_id, String request,
						   boolean persistent, CefQueryCallback callback) {
		Map<String, Object> apis = apisSupplier.get();
		if(jsApiCache == null || apis != lastApis) {
			jsApiCache = jsCacheBuilder.apply(apisSupplier.get());
			lastApis = apis;
		}

		if("--getApis".equals(request)) {
			callback.success(jsApiCache.getApiDescription());
			return true;
		}

		if(request.startsWith("--call:")) {
			Gson gson = new Gson();
			FunctionCall functionCall = gson.fromJson(request.substring("--call:".length()), FunctionCall.class);

			ObjectDescription objectDescription = jsApiCache.call(functionCall);

			callback.success(objectDescription.describe());
			return true;
		}

		return false;
	}
}
