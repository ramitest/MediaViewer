    $(document).ready(function(){
        $('video').mediaelementplayer({});
    });

    $(document).bind('mousewheel', function(e){
        var scope = angular.element($("#bodyElem")).scope();
        if(e.originalEvent.wheelDelta /120 < 0)
            scope.scrollUp();
        else
            scope.scrollDown();
        scope.$apply();
    });

    var app = angular.module('myNavMenu', []);
    app.controller('myNavMenuCont', function($scope, $http) {
        $http.get("MediaViewer/getvideodirs")
            .success(function(response) {
                $scope.imgs = response;
            });
        $scope.menuClick = function(path) {
            $http.get("MediaViewer/getdirvideos?directory="+path)
                .success(function(response) {
                    $("#vidlistDiv").fadeOut(0);
                    $scope.videoList = response;
                    $("#vidlistDiv").fadeIn(800);
                });
        };
        $scope.videoSelected = function(video) {
            $("#vidPlayer").fadeOut(800);
            setTimeout( function() {
                $("video").attr("src", video.src);
                $("#vidPlayer").fadeIn(800);
            }, 800);
        };
        $scope.keyDown = function($event) {
            if ($event.keyCode == 38)
                $scope.scrollDown();
            else if ($event.keyCode == 40)
                $scope.scrollUp();
        };
        $scope.scrollUp = function() {
            if($scope.videoList != undefined)
                $scope.videoList.push( $scope.videoList.shift() );
        };
        $scope.scrollDown = function() {
            if($scope.videoList != undefined)
                $scope.videoList.unshift( $scope.videoList.pop() );
        };
    });
