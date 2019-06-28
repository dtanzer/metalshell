package net.davidtanzer.metalshell;

import org.cef.browser.CefBrowser;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import javax.swing.JFrame;
import java.util.function.Consumer;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class DevToolsTest {
	@Test
	void opensDevToolsAfterBrowserWasInitializedCompletely() {
		FrameFactory frameFactory = mock(FrameFactory.class);
		DevTools devTools = new DevTools(frameFactory);
		Browser browser = mock(Browser.class);
		CefBrowser browserDevTools = mock(CefBrowser.class);

		devTools.addBrowser("ignore", browser, browserDevTools);
		ArgumentCaptor<Consumer<Browser>> createHandler = ArgumentCaptor.forClass(Consumer.class);
		verify(browser).addCreatedHandler(createHandler.capture());
		createHandler.getValue().accept(browser);

		verify(frameFactory).createFrameFor(devTools, browser, browserDevTools);
	}

	@Test
	void closesDevToolsFrameWhenRemovingBrowser() {
		FrameFactory frameFactory = mock(FrameFactory.class);
		JFrame frame = mock(JFrame.class);
		when(frameFactory.createFrameFor(any(), any(), any())).thenReturn(frame);
		DevTools devTools = new DevTools(frameFactory);
		Browser browser = mock(Browser.class);
		CefBrowser browserDevTools = mock(CefBrowser.class);

		devTools.addBrowser("ignore", browser, browserDevTools);
		ArgumentCaptor<Consumer<Browser>> createHandler = ArgumentCaptor.forClass(Consumer.class);
		verify(browser).addCreatedHandler(createHandler.capture());
		createHandler.getValue().accept(browser);
		devTools.removeBrowser(browser, () -> { /* ignore */ });

		ArgumentCaptor<Runnable> afterClose = ArgumentCaptor.forClass(Runnable.class);
		verify(browserDevTools).close(eq(false), afterClose.capture());
		afterClose.getValue().run();
		verify(frame).dispose();
	}

	@Test
	void closesBrowserDevToolsWhenRemovingBrowser() {
		FrameFactory frameFactory = mock(FrameFactory.class);
		JFrame frame = mock(JFrame.class);
		when(frameFactory.createFrameFor(any(), any(), any())).thenReturn(frame);
		DevTools devTools = new DevTools(frameFactory);
		Browser browser = mock(Browser.class);
		CefBrowser browserDevTools = mock(CefBrowser.class);

		devTools.addBrowser("ignore", browser, browserDevTools);
		ArgumentCaptor<Consumer<Browser>> createHandler = ArgumentCaptor.forClass(Consumer.class);
		verify(browser).addCreatedHandler(createHandler.capture());
		createHandler.getValue().accept(browser);
		devTools.removeBrowser(browser, () -> { /* ignore */ });

		ArgumentCaptor<Runnable> afterClose = ArgumentCaptor.forClass(Runnable.class);
		verify(browserDevTools).close(eq(false), afterClose.capture());
		afterClose.getValue().run();
		verify(browserDevTools).close(true);
	}

	@Test
	void runsAfterCloseWhenRemovingBrowser() {
		FrameFactory frameFactory = mock(FrameFactory.class);
		JFrame frame = mock(JFrame.class);
		when(frameFactory.createFrameFor(any(), any(), any())).thenReturn(frame);
		DevTools devTools = new DevTools(frameFactory);
		Browser browser = mock(Browser.class);
		CefBrowser browserDevTools = mock(CefBrowser.class);

		devTools.addBrowser("ignore", browser, browserDevTools);
		ArgumentCaptor<Consumer<Browser>> createHandler = ArgumentCaptor.forClass(Consumer.class);
		verify(browser).addCreatedHandler(createHandler.capture());
		createHandler.getValue().accept(browser);
		Runnable afterDevToolsClose = mock(Runnable.class);
		devTools.removeBrowser(browser, afterDevToolsClose);

		ArgumentCaptor<Runnable> afterClose = ArgumentCaptor.forClass(Runnable.class);
		verify(browserDevTools).close(eq(false), afterClose.capture());
		afterClose.getValue().run();
		verify(afterDevToolsClose).run();
	}

	@Test
	void runsAfterCloseWhenDevToolsAreAlreadyRemoved() {
		FrameFactory frameFactory = mock(FrameFactory.class);
		JFrame frame = mock(JFrame.class);
		when(frameFactory.createFrameFor(any(), any(), any())).thenReturn(frame);
		DevTools devTools = new DevTools(frameFactory);
		Browser browser = mock(Browser.class);
		CefBrowser browserDevTools = mock(CefBrowser.class);

		devTools.addBrowser("ignore", browser, browserDevTools);
		ArgumentCaptor<Consumer<Browser>> createHandler = ArgumentCaptor.forClass(Consumer.class);
		verify(browser).addCreatedHandler(createHandler.capture());
		createHandler.getValue().accept(browser);
		Runnable afterDevToolsClose_Ignore = mock(Runnable.class);
		devTools.removeBrowser(browser, afterDevToolsClose_Ignore);

		Runnable afterDevToolsClose = mock(Runnable.class);
		devTools.removeBrowser(browser, afterDevToolsClose);

		verify(afterDevToolsClose).run();
	}
}