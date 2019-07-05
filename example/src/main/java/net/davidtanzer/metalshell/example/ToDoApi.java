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
package net.davidtanzer.metalshell.example;

import net.davidtanzer.metalshell.jsapi.JsArray;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ToDoApi {
	private final EditWindow editWindow;
	private final TodosUiApi todosUiApi;
	private final List<Todo> todos = new ArrayList<>();

	public ToDoApi(EditWindow editWindow, TodosUiApi todosUiApi) {
		this.editWindow = editWindow;
		this.todosUiApi = todosUiApi;
	}

	public void create() {
		System.out.println("Switching to 2nd window to create a TODO...");
		editWindow.focus();
	}

	public void cancel() {
		editWindow.close();
	}

	public void save(String text, String details) {
		todos.add(new Todo(text, details));
		editWindow.close();
		todosUiApi.updateTodos();
	}

	public TodoArray getTodos() {
		return new TodoArray(todos);
	}

	private class TodoArray extends JsArray<Todo> {
		public TodoArray(List<Todo> todos) {
			super(todos);
		}
	}
}
