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
package net.davidtanzer.metalshell;

import org.cef.CefClient;
import org.cef.browser.CefMessageRouter;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

class APIs {
	private final Map<String, Object> apis = new HashMap<>();
	private Map<String, Object> cachedApis = Collections.unmodifiableMap(apis);
	private final Function<Supplier<Map<String, Object>>, CefMessageRouter> createMessageRouterForApis;

	APIs(Function<Supplier<Map<String, Object>>, CefMessageRouter> createMessageRouterForApis) {
		this.createMessageRouterForApis = createMessageRouterForApis;
	}

	void register(String baseName, Object api) {
		if(!baseName.matches("[a-z_][a-zA-Z_0-9]*")) {
			throw new IllegalArgumentException("Only JavaScript identifiers are allowed for baseName");
		}
		apis.put(baseName, api);
		cachedApis = Collections.unmodifiableMap(apis);
	}

	void addMessageRouters(CefClient client) {
		CefMessageRouter msgRouter = createMessageRouterForApis.apply(() -> cachedApis);
		client.addMessageRouter(msgRouter);
	}
}
