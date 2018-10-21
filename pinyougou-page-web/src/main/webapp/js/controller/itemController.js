 //控制层
app.controller('itemController' ,function($scope,$http){

    $scope.specificationItems={};//记录用户选择的规格
	$scope.sku={};
    $scope.goodsCount=1;
	$scope.addCount=function (num) {
		$scope.goodsCount+=num;
		if ($scope.goodsCount<1){
            $scope.goodsCount=1;
		}
    }

    $scope.selectSpecification=function (key,value) {
        $scope.specificationItems[key]=value;
        $scope.searchSku();
    }

	$scope.loadSku=function () {
        $scope.sku=skuList[0];  //页面初始加载，页面默认显示第一条商品信息
        //$scope.specificationItems=JSON.parse(JSON.stringify($scope.sku.spec));
    }

    matchObject=function (obj1,obj2) {
			for (var key1 in obj1){
				if (obj1[key1]!=obj2[key1]){
                    console.log(false)
					return false;
				}
			}
        for (var key2 in obj2){
            if (obj1[key2]!=obj2[key2]){
                console.log(false)
                return false;
            }
        }


		console.log(false)
		return true;
    }

    $scope.searchSku=function () {
		for (var j=0;j<skuList.length;j++){
			if (matchObject(skuList[j].spec,$scope.specificationItems)){
				console.log($scope.specificationItems)
				$scope.sku=skuList[j];
				console.log(skuList[j]);
				console.log(true,j)
				return;
			}
		}


    }
    
    
    $scope.addCart=function (itemId) {
		$http.get("http://localhost:8086/cart/addGoodsToCartList.do?itemId="+itemId+"&num="+parseInt($scope.goodsCount),{'withCredentials':true}).success(function (data) {
			if (data.status){
				location.href="http://localhost:8086/cart.html";
			}
        })
    }

});
