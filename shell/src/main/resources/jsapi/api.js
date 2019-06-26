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
import {ApiHandler} from "./ApiHandler";

function loadApis() {
	const result = new Promise((resolve, reject) => {
		window.cefQuery({
			request: '--getApis',
			onSuccess: (response) => resolve(response),
			onFailure: (errorCode, errorMessage) => reject(errorCode, errorMessage),
		});
	});

	return result;
}

function callToCef(functionCall) {
	const result = new Promise((resolve, reject) => {
		window.cefQuery({
			request: '--call:'+JSON.stringify(functionCall),
			onSuccess: (response) => resolve(response),
			onFailure: (errorCode, errorMessage) => reject(errorCode, errorMessage),
		});
	});

	return result;
}

async function callCefApi(functionCall) {
	return await callToCef(functionCall);
}

window.callApi = async (callback) => {
	//Should we cache this?
	const response = await loadApis();

	const handler = new ApiHandler(response, callCefApi);

	callback(handler);
};

window.registerUiApi = (apiId, apiObject) => {
	if(window.uiApi == null) {
		window.uiApi = {};
	}

	window.uiApi[apiId] = apiObject;
};
