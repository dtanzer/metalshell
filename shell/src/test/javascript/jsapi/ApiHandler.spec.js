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
import { ApiHandler } from "../../../main/resources/jsapi/ApiHandler";

/*"{
"exampleApi": {
	"type": "object",
		"class": "net.davidtanzer.metalshell.example.ExampleApi"
},
"--classes": {
	"net.davidtanzer.metalshell.example.ExampleApi": {
		"doSomething": {
			"type": "function"
		},
		"getData": {
			"type": "function"
		}
	}
}
}"*/

describe('ApiHandler', () => {
	describe('toplevel structure', () => {
		it('contains a property for every registered API', () => {
			const handler = new ApiHandler('{ "foo": { "type": "object", "class": "Bar" }, "--classes": { "Bar": {}} }');

			expect(handler.foo).toBeDefined();
		});

		it('does not contain a property for fields starting with --', () => {
			const handler = new ApiHandler('{ "--foo": { "type": "object", "class": "Bar" } }');

			expect(handler['--foo']).not.toBeDefined();
		});
	});

	describe('proxied objects', () => {
		it('contains a function for each function on the class', () => {
			const handler = new ApiHandler(`{ 
				"foo": { "type": "object", "class": "Bar" },
				"--classes": { "Bar": { "f": { "type": "function" }}}
			}`);

			expect(handler.foo.f).toBeDefined();
			expect(typeof handler.foo.f).toEqual('function');
		});

		it('passes a formatted function call to the api callback when calling a function on a toplevel object', () => {
			const callback = jest.fn();
			const handler = new ApiHandler(`{ 
				"foo": { "type": "object", "class": "Bar" },
				"--classes": { "Bar": { "f": { "type": "function" }}}
			}`, callback);

			handler.foo.f('a', 5, true);

			expect(callback.mock.calls.length).toBe(1);
			expect(callback.mock.calls[0][0]).toStrictEqual({
				object: 'foo',
				function: 'f',
				args: [ 'a', 5, true],
			});
		});

		xit('throws an error when the type of a toplevel property is not "object"', () => {});
	});

	describe('object description from return value', () => {
		// {
		// 	"--type": "array",
		// 	"--id": "1",
		// 	"--item-type": "net.davidtanzer.metalshell.example.Todo",
		// 	"items": [
		// 		{
		// 			"--id": "2",
		// 			"--type": "net.davidtanzer.metalshell.example.Todo"
		// 		}
		// 		]
		// }

		it('has functions that were described in object class', () => {
			const callback = jest.fn();
			const handler = new ApiHandler(`{ 
				"--classes": { "Bar": { "f": { "type": "function" }}}
			}`, callback);

			const desc = handler._createObjectHandler({'--id':'5','--type':'Bar'});

			expect(typeof desc.f).toBe('function');
		});

		it('calls into function callback with correct object id', () => {
			const callback = jest.fn();
			const handler = new ApiHandler(`{ 
				"--classes": { "Bar": { "f": { "type": "function" }}}
			}`, callback);

			const desc = handler._createObjectHandler({'--id':'5','--type':'Bar'});
			desc.f('b', 7);

			expect(callback.mock.calls.length).toBe(1);
			expect(callback.mock.calls[0][0]).toStrictEqual({
				object: '5',
				function: 'f',
				args: [ 'b', 7],
			});
		});

		it('returns an array of objects when type is array', () => {
			const callback = jest.fn();
			const handler = new ApiHandler(`{ 
				"--classes": { "Bar": { "f": { "type": "function" }}}
			}`, callback);

			const desc = handler._createObjectHandler({
				"--type": "array",
				"--id": "1",
				"--item-type": "net.davidtanzer.metalshell.example.Todo",
				"items": [{'--id': '5', '--type': 'Bar'}]
			});

			expect(desc.length).toBe(1);
			expect(typeof desc[0].f).toBe('function');
		});
	});

	describe('calling functions on arbitrary objects', () => {
		xit('returns primitive return values as javascript primitives', () => {});
		xit('returns return value as javascript object, bound to java object', () => {});
		xit('allows calling a function on the return value of another function', () => {});
	});
});