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
package net.davidtanzer.metalshell.application;

import net.davidtanzer.metalshell.resource.StreamingResource;
import org.cef.browser.CefBrowser;
import org.cef.browser.CefFrame;
import org.cef.callback.CefCallback;
import org.cef.callback.CefSchemeHandlerFactory;
import org.cef.handler.CefResourceHandler;
import org.cef.handler.CefResourceHandlerAdapter;
import org.cef.misc.IntRef;
import org.cef.misc.StringRef;
import org.cef.network.CefRequest;
import org.cef.network.CefResponse;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class AssetsSchemeHandler extends CefResourceHandlerAdapter {
	public static final String SCHEME = "assets";
	public static final String DOMAIN = "assets";
	public static final CefSchemeHandlerFactory FACTORY = AssetsSchemeHandler::createStreamHandler;

	private static CefResourceHandler createStreamHandler(CefBrowser cefBrowser, CefFrame cefFrame, String s, CefRequest cefRequest) {
		String resource = cefRequest.getURL().substring("assets://assets/".length());
		File localFile = new File("src/main/resources/assets/"+resource);

		if(localFile.exists()) {
			try {
				return new StreamingResource(new FileInputStream(localFile));
			} catch (FileNotFoundException e) {
				throw new IllegalStateException(e);
			}
		}

		//TODO add fallbacks...
		return null;
	}
}
