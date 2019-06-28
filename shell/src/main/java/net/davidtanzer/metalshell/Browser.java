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
import org.cef.handler.CefLifeSpanHandlerAdapter;

import java.awt.Component;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

public class Browser {
	private final CefBrowser nativeBrowser;
	private final Configuration config;
	private final CefClient client;

	private CefBrowser browserDevTools;
	private final String name;
	private Component browserUI;
	private DevTools devTools;
	private Set<Consumer<Browser>> createdHandlers = new HashSet<>();
	private Set<Consumer<Browser>> closedHandlers = new HashSet<>();
	private boolean nativeBrowserCreated = false;

	Browser(String name, Configuration config, CefClient client) {
		this.name = name;
		this.config = config;
		this.client = client;

		client.addContextMenuHandler(new CefContextMenuHandlerAdapter() {
			@Override
			public void onBeforeContextMenu(CefBrowser cefBrowser, CefFrame cefFrame, CefContextMenuParams cefContextMenuParams, CefMenuModel cefMenuModel) {
				cefMenuModel.clear();
			}
		});

		nativeBrowser = client.createBrowser( config.getEntryPoint(), OS.isLinux(), true);
		client.addLifeSpanHandler(new LifeSpanHandler());
	}

	public Component getBrowserUi() {
		if(browserUI == null) {
			browserUI = nativeBrowser.getUIComponent();
		}

		return browserUI;
	}

	public <T> T assumeUiApi(String apiId, Class<T> api) {
		InvocationHandler handler = (self, method, args) -> {
			nativeBrowser.executeJavaScript("window.uiApi."+apiId+"."+method.getName()+"("+collectArgs(args)+");", "", 0);
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

	void connectToDevTools(DevTools devTools) {
		this.devTools = devTools;
		browserDevTools = nativeBrowser.getDevTools();
		devTools.addBrowser(name, this, this.browserDevTools);
	}

	public void close(Runnable onAfterClose) {
		if(browserDevTools != null) {
			this.devTools.removeBrowser(this, () -> {
				closeNativeBrowser(onAfterClose);
			});
		} else {
			closeNativeBrowser(onAfterClose);
		}
	}

	public CefBrowser getNativeBrowser() {
		return nativeBrowser;
	}

	private void closeNativeBrowser(Runnable onAfterClose) {
		nativeBrowser.close(false, () -> {
			nativeBrowser.setCloseAllowed();
			nativeBrowser.close(true);
			onAfterClose.run();
			closedHandlers.stream().forEach(h -> h.accept(this));
		});
	}

	@Override
	public String toString() {
		return "{ \"type\": \"MetalShell Browser\", \"name\": \""+name+"\", \"config\": "+config+"}";
	}

	public void addCreatedHandler(Consumer<Browser> createdHandler) {
		this.createdHandlers.add(createdHandler);
		if(nativeBrowserCreated) {
			createdHandler.accept(this);
		}
	}

	public String getName() {
		return name;
	}

	public void addClosedHandler(Consumer<Browser> browserClosed) {
		closedHandlers.add(browserClosed);
	}

	private class LifeSpanHandler extends CefLifeSpanHandlerAdapter {
		@Override
		public void onAfterCreated(CefBrowser cefBrowser) {
			if(cefBrowser != nativeBrowser) {
				return;
			}

			nativeBrowserCreated = true;
			createdHandlers.stream().forEach(h -> h.accept(Browser.this));
		}

		@Override
		public boolean doClose(CefBrowser cefBrowser) {
			return cefBrowser.doClose();
		}
	}
}
