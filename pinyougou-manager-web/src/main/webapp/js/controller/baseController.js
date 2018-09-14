app.controller("baseController",function ($scope) {
    //分页属性设置
    $scope.paginationConf={
        currentPage: 1,
        totalItems: 10,
        itemsPerPage: 10,
        perPageOptions: [10, 20, 30, 40, 50],
        onChange: function(){
            $scope.reloadList();//重新加载
        }
    };
//页面重载
    $scope.reloadList=function () {
        $scope.search($scope.paginationConf.currentPage,$scope.paginationConf.itemsPerPage);
    };

//记录待删除品牌项的id数组
    $scope.selectIds=[];
    $scope.updateSelection=function (e,id) {
        if (e.target.checked){
            $scope.selectIds.push(id);
        }else {
            $scope.selectIds.splice($scope.selectIds.indexOf(id),1);
        }
    }
});