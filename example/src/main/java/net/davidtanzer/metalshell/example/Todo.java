package net.davidtanzer.metalshell.example;

import java.util.UUID;

public class Todo {
	private final String text;
	private final String details;
	private final UUID id;

	public Todo(String text, String details) {
		this.id = UUID.randomUUID();
		this.text = text;
		this.details = details;
	}

	public String getText() {
		return text;
	}

	public String getDetails() {
		return details;
	}
}
