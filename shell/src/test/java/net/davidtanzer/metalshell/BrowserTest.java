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
import org.cef.browser.CefBrowser;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assumptions.assumeThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.Mockito.*;

class BrowserTest {
	@Test
	void assumeUiApiReturnsImplementationOfInterface() {
		Configuration config = mock(Configuration.class);
		CefClient client = mock(CefClient.class);
		when(client.createBrowser(any(), anyBoolean(), anyBoolean())).thenReturn(mock(CefBrowser.class));
		Browser browser = new Browser(config, client);

		TestUiApi api = browser.assumeUiApi("foo", TestUiApi.class);

		assertThat(api).isNotNull();
	}

	@Test
	void callsIntoJavaScriptWhenCallingAnApiMethodWithoutParameters() {
		Configuration config = mock(Configuration.class);
		CefClient client = mock(CefClient.class);
		CefBrowser cefBrowser = mock(CefBrowser.class);
		when(client.createBrowser(any(), anyBoolean(), anyBoolean())).thenReturn(cefBrowser);
		Browser browser = new Browser(config, client);

		TestUiApi api = browser.assumeUiApi("foo", TestUiApi.class);
		assumeThat(api).isNotNull();
		api.callSimpleJsFunction();

		verify(cefBrowser).executeJavaScript("window.uiApi.foo.callSimpleJsFunction();", "", 0);
	}

	@Test
	void callsIntoJavaScriptWhenCallingAnApiMethodWithParameters() {
		Configuration config = mock(Configuration.class);
		CefClient client = mock(CefClient.class);
		CefBrowser cefBrowser = mock(CefBrowser.class);
		when(client.createBrowser(any(), anyBoolean(), anyBoolean())).thenReturn(cefBrowser);
		Browser browser = new Browser(config, client);

		TestUiApi api = browser.assumeUiApi("foo", TestUiApi.class);
		assumeThat(api).isNotNull();
		api.callSomeJsFunction("foo", 0, false);

		verify(cefBrowser).executeJavaScript("window.uiApi.foo.callSomeJsFunction(\"foo\", 0, false);", "", 0);
	}

	private interface TestUiApi {
		void callSomeJsFunction(String foo, int i, boolean b);

		void callSimpleJsFunction();
	}
}