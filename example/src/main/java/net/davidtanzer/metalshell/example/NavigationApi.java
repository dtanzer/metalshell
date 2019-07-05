package net.davidtanzer.metalshell.example;

public class NavigationApi {
	private final NavigationListener navigationListener;

	public NavigationApi(NavigationListener navigationListener) {
		this.navigationListener = navigationListener;
	}

	public void todo() {
		navigationListener.toTodo();
	}

	public void details() {
		navigationListener.toDetails();
	}

	public void help() {
		navigationListener.toHelp();
	}
}
