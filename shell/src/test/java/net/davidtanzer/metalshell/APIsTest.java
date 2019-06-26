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
import org.cef.browser.CefMessageRouter;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatcher;

import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

class APIsTest {
	@Test
	public void onlyAcceptsValidIdentifiersWhileRegistering() {
		assertThatThrownBy(() -> new APIs(mock(Function.class)).register("a-b.c", new Object()))
				.hasMessage("Only JavaScript identifiers are allowed for baseName");
	}

	@Test
	public void passesRegisteredApisToHandlerCreator() {
		Function messageRouterForApis = mock(Function.class);
		APIs apis = new APIs(messageRouterForApis);

		apis.register("test", new Object());
		apis.addMessageRouters(mock(CefClient.class));

		verify(messageRouterForApis).apply(argThat((Supplier<Map<String, Object>> mapSupplier)
				-> mapSupplier.get().containsKey("test")));
	}

	@Test
	public void passesCreatedCefMessageRouterToCefClient() {
		Function messageRouterForApis = mock(Function.class);
		CefMessageRouter messageRouter = mock(CefMessageRouter.class);
		when(messageRouterForApis.apply(any())).thenReturn(messageRouter);
		APIs apis = new APIs(messageRouterForApis);
		CefClient cefClient = mock(CefClient.class);

		apis.register("test", new Object());
		apis.addMessageRouters(cefClient);

		verify(cefClient).addMessageRouter(messageRouter);
	}

	@Test
	public void cachesTheUnmodifiableApisMapWhenNothingHasChanged() {
		Function messageRouterForApis = mock(Function.class);
		APIs apis = new APIs(messageRouterForApis);

		apis.register("test", new Object());
		apis.addMessageRouters(mock(CefClient.class));

		ArgumentCaptor<Supplier<Map<String, Object>>> captor = ArgumentCaptor.forClass(Supplier.class);
		verify(messageRouterForApis).apply(captor.capture());

		Map<String, Object> invocation1 = captor.getValue().get();
		Map<String, Object> invocation2 = captor.getValue().get();

		assertThat(invocation1).isSameAs(invocation2);
	}

	@Test
	public void updatesTheUnmodifiableApisMapWhenSomethingHasChanged() {
		Function messageRouterForApis = mock(Function.class);
		APIs apis = new APIs(messageRouterForApis);

		apis.register("test", new Object());
		apis.addMessageRouters(mock(CefClient.class));

		ArgumentCaptor<Supplier<Map<String, Object>>> captor = ArgumentCaptor.forClass(Supplier.class);
		verify(messageRouterForApis).apply(captor.capture());

		Map<String, Object> invocation1 = captor.getValue().get();
		apis.register("test2", new Object());
		Map<String, Object> invocation2 = captor.getValue().get();

		assertThat(invocation1).isNotSameAs(invocation2);
	}
}