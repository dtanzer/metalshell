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

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.function.Consumer;

public class BrowserWindow {
	private final JFrame mainFrame;
	private final Browser browser;

	BrowserWindow(Browser browser, Consumer<WindowEvent> onClose) {
		this.browser = browser;

		mainFrame = new JFrame();
		mainFrame.getContentPane().add(browser.getBrowserUi(), BorderLayout.CENTER);
		mainFrame.setSize(800, 600);

		mainFrame.addWindowListener(new WindowAdapter() {
			@Override public void windowClosing(WindowEvent e) {
				mainFrame.dispose();
				onClose.accept(e);
				// Alternative: CefApp.getInstance().dispose();
			}
		});

	}

	public void show() {
		mainFrame.setVisible(true);
	}

	public <T> T assumeUiApi(String apiId, Class<T> api) {
		return browser.assumeUiApi(apiId, api);
	}
}
