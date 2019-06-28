package net.davidtanzer.metalshell.example;

import net.davidtanzer.metalshell.Browser;
import net.davidtanzer.metalshell.Configuration;
import net.davidtanzer.metalshell.MetalShell;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class SecondWindow {
	private final MetalShell shell;
	private final Configuration.Builder configBuilder;
	private JFrame frame;
	private Component browserForTabsUi;
	private Component mainBrowserUi;

	public SecondWindow(MetalShell shell, Configuration.Builder configBuilder) {
		this.shell = shell;
		this.configBuilder = configBuilder;
	}

	public void focus() {
		frame = new JFrame("Second Window");
		frame.setContentPane(new JPanel(new BorderLayout()));

		Configuration tabsConfig = configBuilder.withEntryPoint("assets://assets/html/tabs.html").configuration();
		Browser browserForTabs = shell.createBrowser("tabs", tabsConfig);

		browserForTabsUi = browserForTabs.getBrowserUi();
		browserForTabsUi.setMinimumSize(new Dimension(100, 50));
		browserForTabsUi.setPreferredSize(new Dimension(100, 50));
		frame.getContentPane().add(browserForTabsUi, BorderLayout.PAGE_START);

		Configuration mainConfig = configBuilder.withEntryPoint("assets://assets/html/second-window.html").configuration();
		Browser mainBrowser = shell.createBrowser("main", mainConfig);
		mainBrowserUi = mainBrowser.getBrowserUi();
		frame.getContentPane().add(mainBrowserUi, BorderLayout.CENTER);

		frame.setSize(800, 600);
		frame.setVisible(true);

		frame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				browserForTabs.close(() -> {
					mainBrowser.close(() -> {
						frame.dispose();
					});
				});
			}
		});
	}
}
