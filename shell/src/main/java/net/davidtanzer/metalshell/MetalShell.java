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

import net.davidtanzer.metalshell.application.ApplicationHandler;
import net.davidtanzer.metalshell.jsapi.JsCacheBuilder;
import net.davidtanzer.metalshell.jsapi.MessageRouterHandler;
import org.cef.CefApp;
import org.cef.CefClient;
import org.cef.CefSettings;
import org.cef.browser.CefMessageRouter;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

public class MetalShell {
	private final JsCacheBuilder jsCacheBuilder = new JsCacheBuilder();
	private final APIs apis = new APIs(apis -> {
		CefMessageRouter msgRouter = CefMessageRouter.create();
		msgRouter.addHandler(new MessageRouterHandler(apis, jsCacheBuilder::buildFrom), true);
		return msgRouter;
	});
	private final DevTools devTools = new DevTools();
	private final Consumer<MetalShell> onLastBrowserClosed;
	private final Set<Browser> registeredBrowsers = new HashSet<>();

	private CefApp cefApp;

	public static MetalShell bootstrap() {
		return bootstrap(shell -> shell.disposeApp());
	}

	public static MetalShell bootstrap(Consumer<MetalShell> onLastBrowserClosed) {
		return new MetalShell(onLastBrowserClosed).initialize();
	}

	private MetalShell(Consumer<MetalShell> onLastBrowserClosed) {
		this.onLastBrowserClosed = onLastBrowserClosed;
	}

	private MetalShell initialize() {
		CefSettings settings = new CefSettings();
		settings.javascript_flags="--harmony-weak-refs --expose-gc";
		settings.windowless_rendering_enabled = false;

		String[] args = {"--javascript-harmony" /*, "--disable-gpu", "--disable-gpu-compositing"*/};
		cefApp = CefApp.getInstance(args, settings);
		cefApp.addAppHandler(new ApplicationHandler(args));

		return this;
	}

	public Configuration.Builder newConfigurationBuilder() {
		return new Configuration.Builder();
	}

	public BrowserWindow createBrowserWindow(String title, Configuration config) {
		Browser browser = createBrowser(title, config);

		return new BrowserWindow(browser, e -> {});
	}

	private void browserClosed(Browser browser) {
		registeredBrowsers.remove(browser);

		if(registeredBrowsers.isEmpty()) {
			onLastBrowserClosed.accept(this);
		}
	}

	public Browser createBrowser(String name, Configuration config) {
		if(cefApp == null) {
			initialize();
		}

		CefClient client = cefApp.createClient();
		apis.addMessageRouters(client);

		Browser browser = new Browser(name, config, client);
		browser.addClosedHandler(this::browserClosed);
		registeredBrowsers.add(browser);

		browser.connectToDevTools(devTools);

		return browser;
	}

	public void registerApi(String baseName, Object api) {
		apis.register(baseName, api);
	}

	private void disposeApp() {
		cefApp.dispose();
		cefApp = null;
	}
}
