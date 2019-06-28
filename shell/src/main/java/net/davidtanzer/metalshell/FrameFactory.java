package net.davidtanzer.metalshell;

import org.cef.browser.CefBrowser;

import javax.swing.JFrame;
import java.awt.BorderLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

class FrameFactory {
	public JFrame createFrameFor(DevTools devTools, Browser browser, CefBrowser browserDevTools) {
		JFrame frame = new JFrame("DevTools: "+browser.getName());

		frame.setSize(800, 600);
		frame.setLocation(800,200);
		frame.getContentPane().add(browserDevTools.getUIComponent(), BorderLayout.CENTER);

		frame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				devTools.removeBrowser(browser);
			}
		});

		frame.setVisible(true);
		return frame;
	}
}
