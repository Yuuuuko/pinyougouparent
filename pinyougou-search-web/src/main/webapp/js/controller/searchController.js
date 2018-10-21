app.controller("searchController",function ($scope,searchService,$sce,$location) {
    //定义搜索条件集合
    $scope.searchMap={'keywords':'','category':'','brand':'','spec':{},'price':'','pageNo':1,'pageSize':40,'sortField':'','sort':''};//搜索对象
    $scope.resultMap={};       //定义接收搜素结果的对象


    $scope.search=function () {
        $scope.searchMap.pageNo=parseInt($scope.searchMap.pageNo);
        searchService.search($scope.searchMap).success(function (data) {
            $scope.resultMap=data;

            buildPageLabel();
        })
    }

    $scope.addSearchItem=function (key,value) {
        if (key==='category'||key==='brand'||key==='price'){
            $scope.searchMap[key]=value;
        }else {
            $scope.searchMap.spec[key]=value;
        }
        //console.log($scope.searchMap);
        $scope.searchMap.pageNo=1;
        $scope.search();
    }


    $scope.removeSearchItem=function(key){
        if(key==="category" ||  key==="brand"||key==='price'){//如果是分类或品牌
            $scope.searchMap[key]="";
        }else{//否则是规格
            delete $scope.searchMap.spec[key];//移除此属性
        }
        $scope.searchMap.pageNo=1;
        $scope.search();
    }

    buildPageLabel=function () {
        $scope.pageLabel=[];       //分页查询数数据
        $scope.maxPage=$scope.resultMap.totalPages;  //最大页码数
        $scope.firstPage=$scope.searchMap.pageNo-2;
        $scope.lastPage=$scope.searchMap.pageNo+2;
        if ($scope.searchMap.pageNo-2<=1){
            $scope.firstPage=1;
            $scope.lastPage=$scope.firstPage+4;
        }
        if ($scope.maxPage<=5){
            $scope.firstPage=1;
            $scope.lastPage=$scope.resultMap.totalPages;
        }
        if ($scope.searchMap.pageNo+2>=$scope.maxPage&&$scope.maxPage>=5){
            $scope.firstPage=$scope.maxPage-4;
            $scope.lastPage=$scope.maxPage;
        }

        for (var i=$scope.firstPage;i<=$scope.lastPage;i++){
            $scope.pageLabel.push(i);
        }

    }

    $scope.queryByPage=function (pageNo) {

        if (pageNo<1||pageNo>$scope.maxPage){
            return;
        }
        $scope.searchMap.pageNo=pageNo;
        $scope.search();
    }

    $scope.addSortFiled=function (sortField,sort) {
        $scope.searchMap.sortField=sortField;
        $scope.searchMap.sort=sort;
        $scope.search();
    }

    $scope.isHiddenBrand=function () {

        for (var i=0;i<$scope.resultMap.brandList.length;i++){
            if ($scope.searchMap.keywords.indexOf($scope.resultMap.brandList[i].text)>=0){
                return true;
            }
        }
        return false;
    }

    $scope.loadSearch=function () {

        $scope.searchMap.keywords=$location.search()['keywords'] ||'';
        $scope.search();
    }
})