'use strict';

var app = angular.module('app', [
	'ngRoute', 'controllers'
]);

app.config(['$route', function($routeProvider) {
	$routeProvider.
	when('/tail', {
		templateUrl: 'tail.html',
		controller: 'TailCtrl'
	}).
	when('/welcome', {
		templateUrl: 'welcome.html',
		controller: 'WelcomeCtrl'
	}).
	otherwise({
		redirectTo: '/welcome'
	});
}])