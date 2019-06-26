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
import org.cef.OS;
import org.cef.browser.CefBrowser;
import org.cef.browser.CefFrame;
import org.cef.callback.CefContextMenuParams;
import org.cef.callback.CefMenuModel;
import org.cef.handler.CefContextMenuHandlerAdapter;

import java.awt.*;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.time.LocalDate;

public class Browser {
	private final CefBrowser browser;
	private final CefBrowser devTools;
	private Component browserUI;
	private Component devToolsUi;

	Browser(Configuration config, CefClient client) {
		client.addContextMenuHandler(new CefContextMenuHandlerAdapter() {
			@Override
			public void onBeforeContextMenu(CefBrowser cefBrowser, CefFrame cefFrame, CefContextMenuParams cefContextMenuParams, CefMenuModel cefMenuModel) {
				cefMenuModel.clear();
			}
		});

		browser = client.createBrowser( config.getEntryPoint(), OS.isLinux(), true);
		devTools = browser.getDevTools();
	}

	public Component getBrowserUi() {
		if(browserUI == null) {
			browserUI = browser.getUIComponent();
		}

		return browserUI;
	}

	Component getDevToolsUi() {
		if(devToolsUi == null) {
			devToolsUi = devTools.getUIComponent();
		}

		return devToolsUi;
	}

	public <T> T assumeUiApi(String apiId, Class<T> api) {
		InvocationHandler handler = (self, method, args) -> {
			browser.executeJavaScript("window.uiApi."+apiId+"."+method.getName()+"("+collectArgs(args)+");", "", 0);
			return null;
		};
		return (T) Proxy.newProxyInstance(api.getClassLoader(), new Class[] {api}, handler);
	}

	private String collectArgs(Object[] args) {
		if(args == null) {
			return "";
		}
		StringBuilder argsBuilder = new StringBuilder();

		for(Object arg : args) {
			if(argsBuilder.length() > 0) {
				argsBuilder.append(", ");
			}

			if(arg instanceof String) {
				argsBuilder.append("\"").append(arg).append("\"");
			} else {
				argsBuilder.append(arg);
			}
		}
		return argsBuilder.toString();
	}
}
