package net.davidtanzer.metalshell.example;

import net.davidtanzer.metalshell.Browser;
import net.davidtanzer.metalshell.Configuration;
import net.davidtanzer.metalshell.MetalShell;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class EditWindow {
	private final MetalShell shell;
	private final Configuration.Builder configBuilder;
	private JFrame frame;
	private Component browserForTabsUi;
	private Component mainBrowserUi;
	private Browser mainBrowser;
	private Browser browserForTabs;

	public EditWindow(MetalShell shell, Configuration.Builder configBuilder) {
		this.shell = shell;
		this.configBuilder = configBuilder;
	}

	public void focus() {
		if(frame == null) {
			createFrame();
		}
		frame.requestFocus();
	}

	private void createFrame() {
		frame = new JFrame("Second Window");
		frame.setContentPane(new JPanel(new BorderLayout()));

		Configuration tabsConfig = configBuilder.withEntryPoint("assets://assets/html/tabs.html").configuration();
		browserForTabs = shell.createBrowser("tabs", tabsConfig);

		browserForTabsUi = browserForTabs.getBrowserUi();
		browserForTabsUi.setMinimumSize(new Dimension(100, 50));
		browserForTabsUi.setPreferredSize(new Dimension(100, 50));
		frame.getContentPane().add(browserForTabsUi, BorderLayout.PAGE_START);

		Configuration mainConfig = configBuilder.withEntryPoint("assets://assets/html/second-window.html").configuration();
		mainBrowser = shell.createBrowser("main", mainConfig);
		mainBrowserUi = mainBrowser.getBrowserUi();
		frame.getContentPane().add(mainBrowserUi, BorderLayout.CENTER);

		NavigationListener navigationListener = mainBrowser.assumeUiApi("navListener", NavigationListener.class);
		shell.registerApi("nav", new NavigationApi(navigationListener));

		frame.setSize(800, 600);
		frame.setVisible(true);

		frame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				closeBrowsersAndFrame();
			}
		});
	}

	private void closeBrowsersAndFrame() {
		browserForTabs.close(() -> {
			mainBrowser.close(() -> {
				frame.dispose();
				frame = null;
			});
		});
	}

	public void close() {
		closeBrowsersAndFrame();
	}
}
