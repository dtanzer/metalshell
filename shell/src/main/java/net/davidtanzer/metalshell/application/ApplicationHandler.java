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
package net.davidtanzer.metalshell.application;

import net.davidtanzer.metalshell.jsapi.JsApiSchemeHandler;
import org.cef.CefApp;
import org.cef.handler.CefAppHandlerAdapter;

public class ApplicationHandler extends CefAppHandlerAdapter {
	public ApplicationHandler() {
		super(new String[0]);
	}

	@Override
	public void onContextInitialized() {
		CefApp cefApp = CefApp.getInstance();

		cefApp.registerSchemeHandlerFactory(
				AssetsSchemeHandler.SCHEME,
				AssetsSchemeHandler.DOMAIN,
				AssetsSchemeHandler.FACTORY);

		cefApp.registerSchemeHandlerFactory(
				JsApiSchemeHandler.SCHEME,
				JsApiSchemeHandler.DOMAIN,
				JsApiSchemeHandler.FACTORY);
	}

}
