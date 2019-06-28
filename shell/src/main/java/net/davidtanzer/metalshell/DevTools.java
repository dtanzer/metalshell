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

import org.cef.browser.CefBrowser;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.HashMap;
import java.util.Map;

class DevTools {
	private final FrameFactory frameFactory;
	private Map<Browser, DevToolsData> registeredBrowsers = new HashMap<>();
	private JTabbedPane tabbedPane;

	DevTools() {
		this(new FrameFactory());
	}

	DevTools(FrameFactory frameFactory) {
		this.frameFactory = frameFactory;
	}

	public void addBrowser(String name, Browser browser, CefBrowser browserDevTools) {
		browser.addCreatedHandler((b) -> {
			JFrame frame = frameFactory.createFrameFor(this, browser, browserDevTools);
			registeredBrowsers.put(browser, new DevToolsData(name, browserDevTools, frame));
		});
	}

	public void removeBrowser(Browser browser) {
		removeBrowser(browser, ()->{});
	}

	public void removeBrowser(Browser browser, Runnable afterClose) {
		DevToolsData devToolsData = registeredBrowsers.remove(browser);

		boolean devToolsAlreadyClosed = devToolsData == null;
		if(devToolsAlreadyClosed) {
			afterClose.run();
			return;
		}

		devToolsData.browserDevTools.close(false, () -> {
			devToolsData.browserDevTools.close(true);
			devToolsData.frame.dispose();
			devToolsData.frame = null;
			afterClose.run();
		});
	}

	private class DevToolsData {
		private final String name;
		private final CefBrowser browserDevTools;
		private JFrame frame;

		public DevToolsData(String name, CefBrowser browserDevTools, JFrame frame) {
			this.name = name;
			this.browserDevTools = browserDevTools;
			this.frame = frame;
		}
	}
}
