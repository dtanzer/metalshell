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

import net.davidtanzer.metalshell.resource.StreamingResource;
import org.cef.browser.CefBrowser;
import org.cef.browser.CefFrame;
import org.cef.callback.CefSchemeHandlerFactory;
import org.cef.handler.CefResourceHandler;
import org.cef.network.CefRequest;

import java.io.InputStream;

public class JsApiSchemeHandler {
	public static final String SCHEME = "jsapi";
	public static final String DOMAIN = "jsapi";
	public static final CefSchemeHandlerFactory FACTORY = JsApiSchemeHandler::createStreamHandler;

	private static CefResourceHandler createStreamHandler(CefBrowser cefBrowser, CefFrame cefFrame, String s, CefRequest cefRequest) {
		String resource = cefRequest.getURL().substring("jsapi://jsapi/".length());

		InputStream stream = JsApiSchemeHandler.class.getClassLoader().getResourceAsStream("jsapi/" + resource);
		if(stream == null) {
			stream = JsApiSchemeHandler.class.getClassLoader().getResourceAsStream("jsapi/" + resource+".js");
		}
		if(stream != null) {
			return new StreamingResource(stream, "application/javascript");
		}

		return null;
	}
}
