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
package net.davidtanzer.metalshell.resource;

import org.cef.callback.CefCallback;
import org.cef.handler.CefResourceHandlerAdapter;
import org.cef.misc.IntRef;
import org.cef.misc.StringRef;
import org.cef.network.CefRequest;
import org.cef.network.CefResponse;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

public class StreamingResource extends CefResourceHandlerAdapter {

	private final InputStream stream;
	private final String mimeType;

	public StreamingResource(InputStream stream, String mimeType) {
		this.stream = stream;
		this.mimeType = mimeType;
	}

	public static StreamingResource fromString(String stringToStream) {
		return fromString(stringToStream, "UTF-8");
	}

	public static StreamingResource fromString(String stringToStream, String charsetName) {
		try {
			return new StreamingResource(new ByteArrayInputStream(stringToStream.getBytes(charsetName)));
		} catch (UnsupportedEncodingException e) {
			throw new IllegalStateException(e);
		}
	}

	public StreamingResource(InputStream stream) {
		this(stream, "text/html");
	}

	@Override
	public boolean processRequest(CefRequest cefRequest, CefCallback cefCallback) {
		cefCallback.Continue();
		return true;
	}

	@Override
	public void getResponseHeaders(CefResponse cefResponse, IntRef length, StringRef redirectUrl) {
		length.set(-1);
		Map<String, String> headers = new HashMap<>();
		headers.put("Access-Control-Allow-Origin", "*");
		cefResponse.setHeaderMap(headers);
		cefResponse.setStatus(200);
		cefResponse.setMimeType(mimeType);
	}

	@Override
	public boolean readResponse(byte[] bytes, int bytesToRead, IntRef bytesRead, CefCallback cefCallback) {
		try {
			int toRead = Math.min(bytesToRead, stream.available());
			if(toRead == 0) {
				return false;
			}
			int numBytesRead = stream.read(bytes, 0, toRead);
			bytesRead.set(numBytesRead);
		} catch (IOException e) {
			throw new IllegalStateException(e);
		}
		return true;
	}
}