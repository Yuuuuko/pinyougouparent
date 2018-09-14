app.controller("brandController",function ($scope,brandService,$controller) {

    $controller("baseController",{$scope:$scope});

    $scope.findBrand=function () {
        brandService.findBrand.success(function (data) {
            $scope.brandList=data;
        })
    };


    //分页查询
    $scope.findPage=function (page,rows) {
        brandService.findPage(page,rows).success(function (data) {
            $scope.brandList=data.rows;
            $scope.paginationConf.totalItems=data.total;
        })
    }
    //添加或修改品牌
    $scope.save=function () {

        var serviceObject;
        if ($scope.entity.id){
            serviceObject=brandService.update($scope.entity);
        }else {
            serviceObject=brandService.insert($scope.entity);
        }
        serviceObject.success(function (data) {

            if(data.status){
                $scope.reloadList();
            }else {
                alert(data.msg);
            }
        })
    }
    //根据id查询一个品牌信息
    $scope.findOne=function (id) {

        brandService.findOne(id).success(function (data) {
            $scope.entity=data;
        })
    }

    //批量删除选中的品牌
    $scope.dele=function () {
        brandService.dele($scope.selectIds).success(function (data) {
            if(data.status){
                $scope.reloadList();
            }else {
                alert(data.msg);
            }
        })
    }
    //条件查询对象
    $scope.searchEntity={};
    $scope.search=function (page,rows) {
        brandService.search(page,rows,$scope.searchEntity).success(function (data) {
            $scope.brandList=data.rows;
            $scope.paginationConf.totalItems=data.total;
        })
    }

})
