'use strict';

/**
 * @ngdoc overview
 * @name promoSiteApp
 * @description
 * # promoSiteApp
 *
 * Main module of the application.
 */
angular
  .module('promoSiteApp', [
    'ngAnimate',
    'ngCookies',
    'ngResource',
    'ngRoute',
    'ngSanitize',
    'ngTouch'
  ])
  .config(function ($routeProvider) {
    $routeProvider
      .when('/', {
        templateUrl: 'views/main.html',
        controller: 'MainCtrl'
      })
      .when('/about', {
        templateUrl: 'views/about.html'
      })
      .when('/contact', {
          templateUrl: 'views/contact.html'
      })
      .otherwise({
        redirectTo: '/'
      });
  });
