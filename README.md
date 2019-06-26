# Metal Shell

Build GUIs for Java / JVM applications with HTML, CSS and JavaScript (think: "Electron, but for Java").

Metal Shell is a **proof of concept** and a playground so far. For now, it **only** works on **Win64** (but adding more operating systems is definitely a priority for me). Do not use it for anything productive yet - But please play around with it and send me your feedback.

## Getting Started

Clone the repository on a Windows 64 machine, and then type

    gradlew example:run
    
into a console window. This will start the example application.

When you import the sources into an IDE (so you can examine them and play around), create a run configuration that includes all the required native libraries. In IntelliJ IDEA, I added the following VM Options:

    -Djava.library.path=$ProjectFileDir$/shell/lib/jcef/win64/native

## Using Metal Shell

Metal shell allows you to create Chromium browser components and browser windows within a Java application. 

### Browsers and HTML

Create a browser window with the default configuration with the following code:

    MetalShell shell = MetalShell.bootstrap();
    Configuration.Builder config = shell.newConfigurationBuilder();
    BrowserWindow browserWindow = shell.createBrowserWindow("Example App", config.configuration());

In those browsers, you can load HTML, CSS and JavaScript from your classpath. In the default configuration, the entry point of your application is:

    assets://assets/html/index.html

Where ```assets://``` is a location that is loaded from your class path. So, put all the UI assets of your application there:

    /your-app
    +--- /src
         +--- /main
              +--- /resources
                   +--- /assets
                        +--- /css
                        |    +--- style.css
                        +--- /html
                             +index.html

In your index.html, you can load relative resources from your own class path:

    <link rel="stylesheet" href="/css/style.css" />

In your Java (or Kotlin or ...) code, call

    browserWindow.show();

to open this browser window. 

### Calling Back to Java Code from JavaScript

To be able to call back into Java from the JavaScript code in your browser, you must register an API with Metal Shell:

    shell.registerApi("exampleApi", new ExampleApi());

Where ExampleApi is a plain Java class that contains code you want to call into from JavaScript:

    public class ExampleApi {
        public void doSomething(String str, int i) {
            System.out.println("Doing something: "+str+", "+i);
        }
    }

To be able to call back into Java, you have to load a bit of JavaScript from Metal Shell itself in your HTML code:

    <script type="module" src="jsapi://jsapi/api.js"></script>

...and then you can call the function you registered above:

    window.callApi(api => api.exampleApi.doSomething('xyz', 17))

where ```exampleApi``` is the name you gave the API when registering it and ```doSomething``` is the name of the Java method on the registered class.

### Callint Into JavaScript Code from Java

If you also want to start code in the Browser from your JVM code, you first must register an interface that you want to call from Java:

    ExampleUiApi uiApi = browserWindow.assumeUiApi("example", ExampleUiApi.class);
    
Where ```ExampleUiApi``` is a Java Interface:

    public interface ExampleUiApi {
        void someUiFunction(String str, int i);
    }

The object in ```uiApi``` is now a proxy: Whenever you call a method on it, the call will be passed to JavaScript and Metal Shell will call a corresponding function there.

So, on the JavaScript side, register a function that can be called (you will need the script ```jsapi://jsapi/api.js``` for registering too):

    window.onload = () => {
        console.log("Registering UI API");
        window.registerUiApi("example", {
            someUiFunction: (x, y) => console.log("some function called from Java:", x, y)
        });
    }

The id ```example``` here must be the same ID that you also used when calling ```assumeUiApi```. And the object you pass into ```registerUiApi``` must have corresponding functions for every method on the Java interface.

Then, when you call a method on the Java interface:

    uiApi.someUiFunction("Data from JAVA!", 1717);

You will see the log line in the console of your debug tools:

    some function called from Java: Data from JAVA! 1717

## Contributing

For now, I do **not** accept pull requests (until I figured out most of the legal implications of accepting other people's code into this project). So please do not send some.

What I need right now is feedback. Please create issues with your requirements / wishes and also for problems you found. That would help a lot.

## Commercially Friendly Licensing

This program is distributed under the AGPL - at least for now, until I know how I really want to license it.

I do understand that this might be an impediment to you adopting this framework. But right now, it is a proof-of-concept anyway and not production ready yet.

If you are interested in getting a more commercially friendly license, please contact me at [business@davidtanzer.net](mailto:business@davidtanzer.net).

## License and Copying

    MetalShell - Create HTML+JS user interfaces for JVM applications
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

See [LICENSE.txt](LICENSE.txt) for the complete text of the license.

This program is based on the Chromium Embedded Framework for Java (java-cef). Please refer to [LICENSE-JAVA-CEF.txt](LICENSE-JAVA-CEF.txt) for the license of Java-CEF.
