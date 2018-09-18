 //控制层 
app.controller('goodsController' ,function($scope,goodsService,uploadService,itemCatService,typeTemplateService){
	


	//参数封装对象初始化，对象内有3个goods相关的属性对象
	$scope.entity={goods:{},goodsDesc:{},itemList:[]};
    $scope.entity={goods:{},goodsDesc:{itemImages:[]}};//定义页面实体结构
    $scope.itemCat1List={};//初始化一级分类对象
    $scope.itemCat2List={};//初始化一级分类对象
    $scope.itemCat3List={};//初始化一级分类对象
    //读取列表数据绑定到表单中
	$scope.findAll=function(){
		goodsService.findAll().success(
			function(response){
				$scope.list=response;
			}			
		);
	}    
	
	//分页
	$scope.findPage=function(page,rows){			
		goodsService.findPage(page,rows).success(
			function(response){
				$scope.list=response.rows;	
				$scope.paginationConf.totalItems=response.total;//更新总记录数
			}			
		);
	}
	
	//查询实体 
	$scope.findOne=function(id){				
		goodsService.findOne(id).success(
			function(response){
				$scope.entity= response;					
			}
		);				
	}
	
	//保存 
	$scope.save=function(){
        $scope.entity.goodsDesc.introduction=editor.html();
		/*var serviceObject;//服务层对象
		if($scope.entity.id!=null){//如果有ID
			serviceObject=goodsService.update( $scope.entity ); //修改  
		}else{
			serviceObject=goodsService.add( $scope.entity  );//增加 
		}		*/
        goodsService.add( $scope.entity).success(
			function(response){
				if(response.status){
					//重新查询 
		        	/*$scope.reloadList();//重新加载*/
					$scope.entity={goods:{},goodsDesc:{},itemList:[]};
                    editor.html('');//清空富文本编辑器
				}else{
					alert(response.msg);
				}
			}		
		);				
	}
	
	 
	//批量删除 
	$scope.dele=function(){			
		//获取选中的复选框			
		goodsService.dele( $scope.selectIds ).success(
			function(response){
				if(response.success){
					$scope.reloadList();//刷新列表
					$scope.selectIds=[];
				}						
			}		
		);				
	}
	
	$scope.searchEntity={};//定义搜索对象 
	
	//搜索
	$scope.search=function(page,rows){			
		goodsService.search(page,rows,$scope.searchEntity).success(
			function(response){
				$scope.list=response.rows;	
				$scope.paginationConf.totalItems=response.total;//更新总记录数
			}			
		);
	}
	//图片上传相关
    $scope.add_image_entity=function(){

        $scope.entity.goodsDesc.itemImages.push($scope.image_entity);
    }

    $scope.remove_image_entity=function(index){
        $scope.entity.goodsDesc.itemImages.splice(index,1);
    }

    $scope.uploadFile=function(){
        uploadService.uploadFile().success(function(response) {
            if(response.status){//如果上传成功，取出 url
                $scope.image_entity.url=response.msg;//设置文件地址
            }else{
                alert(response.msg);
            }
        }).error(function() {

            alert("上传发生错误");
        });
    };
	//获取一级分类
	$scope.findItemCatListByParentId=function () {

		itemCatService.findByParentId(0).success(function (data) {
            $scope.itemCat1List=data.rows;
        })
    }
	//获取二级分类
	$scope.$watch('entity.goods.category1Id',function (newValue,oldValue) {
        //console.log(newValue);
		$scope.entity.goods.category2Id=-1;
		if (newValue){
            itemCatService.findByParentId(newValue).success(function (data) {
                $scope.itemCat2List=data.rows;
            })
		}
    })

	//获取三级分类
    $scope.$watch('entity.goods.category2Id',function (newValue,oldValue) {
    	//console.log(newValue);
        if (newValue){
            itemCatService.findByParentId(newValue).success(function (data) {
                $scope.itemCat3List=data.rows;
            })
        }
    })
	//获取摸版id
    $scope.$watch('entity.goods.category3Id',function (newValue,oldValue) {
        if (newValue){
            itemCatService.findOne(newValue).success(function (data) {
                $scope.entity.goods.typeTemplateId=data.typeId;
            })
        }
    })


    
});	
