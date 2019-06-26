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
export class ApiHandler {
	constructor(apiDescriptionString, functionCallback) {
		const handler = this;
		const apiDescription = JSON.parse(apiDescriptionString);

		handler.classes = apiDescription['--classes'];
		handler.functionCallback = functionCallback;

		Object.keys(apiDescription).forEach(k => {
			if(!k.startsWith('--')) {
				const api = {};
				const apiClass = handler.classes[apiDescription[k]['class']];

				Object.keys(apiClass).forEach(classProp => {
					api[classProp] = new Proxy(function () {}, {
						apply: (target, thisArg, args) => {
							handler.functionCallback({
								object: k,
								function: classProp,
								args,
							});
						},
					});
				});

				handler[k] = api;
			}
		});
	}
}
