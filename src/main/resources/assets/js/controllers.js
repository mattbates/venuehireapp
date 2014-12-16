angular.module('controllers', [])
    .controller('SearchCtrl', ['$scope', '$http', function($scope, $http) {
   $scope.venues = [];
   $scope.search = function() {
       $http.get('http://localhost:8080/api/venuesearch/by-text?searchtext=' + $scope.searchTerm).
         success(function(data) {
           $scope.venues = data;
         });
     };
    $scope.searchbyPostcode = function() {
           $http.get('http://localhost:8080/api/venuesearch/by-postcode?postcode=' + $scope.postcode).
             success(function(data) {
               $scope.venues = data;
             });
         };

}]);
