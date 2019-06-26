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

import org.cef.misc.IntRef;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class StreamingResourceTest {
	@Test
	public void returnsDataAvailableAfterReadingFromStream() {
		StreamingResource resource = StreamingResource.fromString("12345");

		byte[] bytes = new byte[5];
		boolean dataAvailable = resource.readResponse(bytes, 5, new IntRef(), null);

		assertThat(dataAvailable).isTrue();
	}

	@Test
	public void fillsBytesWithReadDataWhenReadingFromStream() {
		StreamingResource resource = StreamingResource.fromString("12345");

		byte[] bytes = new byte[5];
		boolean dataAvailable = resource.readResponse(bytes, 5, new IntRef(), null);

		assertThat(new String(bytes)).isEqualTo("12345");
	}

	@Test
	public void fillyBytesReadWithNumberOfReadBytes() {
		StreamingResource resource = StreamingResource.fromString("12345");

		byte[] bytes = new byte[10];
		IntRef bytesRead = new IntRef();
		boolean dataAvailable = resource.readResponse(bytes, 10, bytesRead, null);

		assertThat(bytesRead.get()).isEqualTo(5);
	}

	@Test
	public void returnsNoDataAvailableWhenReadingPastEnd() {
		StreamingResource resource = StreamingResource.fromString("12345");

		byte[] bytes = new byte[5];
		resource.readResponse(bytes, 5, new IntRef(), null);
		boolean dataAvailable = resource.readResponse(bytes, 5, new IntRef(), null);

		assertThat(dataAvailable).isFalse();
	}
}