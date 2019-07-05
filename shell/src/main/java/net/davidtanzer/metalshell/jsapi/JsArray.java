package net.davidtanzer.metalshell.jsapi;

import java.util.Collections;
import java.util.List;

public class JsArray<T> {
	private final List<T> items;

	public JsArray(List<T> items) {
		this.items = Collections.unmodifiableList(items);
	}

	public List<T> getItems() {
		return items;
	}
}
