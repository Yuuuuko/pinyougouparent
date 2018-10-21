app.controller("contentController",function ($scope,contentService) {
    //存放首页轮播广告
    //carouselContent轮播图广告
    $scope.content=[];
    $scope.carouselContent=[];
    $scope.keywords=''; //搜索关键字

    //根据广告分类ID查询广告列表
    $scope.findByCategoryId=function (categoryId) {
        contentService.findByCategoryId(categoryId).success(function (data) {
            $scope.content[categoryId]=data;
            console.log($scope.content[categoryId]);
        })
    }
    //查询所有广告
    $scope.findAllContent=function () {
        contentService.findAll().success(function (data) {
            $scope.content=data;
        })
    }


    $scope.search=function () {
        console.log($scope.keywords);
        location.href="http://localhost:8084/search.html#?keywords="+$scope.keywords;
    }


})