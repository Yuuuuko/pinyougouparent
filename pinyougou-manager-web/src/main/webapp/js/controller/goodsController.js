 //控制层 
app.controller('goodsController' ,function($scope,goodsService,itemCatService,typeTemplateService,$controller){

    $controller("baseController",{$scope:$scope});
	//参数封装对象初始化，对象内有3个goods相关的属性对象
    //定义页面实体结构
    $scope.entity={
    	goods:{},
		goodsDesc:{
    		itemImages:[], //商品图片集合
			customAttributeItems:[], //商品扩展属性集合
            specificationItems:[]   //商品规格集合
		},
        itemList:[]
    };
	$scope.typeTemplate={brandIds:[],customAttributeItems:[]};
    $scope.itemCat1List={};//初始化一级分类对象
    $scope.itemCat2List={};//初始化一级分类对象
    $scope.itemCat3List={};//初始化一级分类对象

    $scope.searchEntity={};//定义搜索对象
	$scope.specList=[];

	$scope.itemCatList=[];//字符串数组，存放所有



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
		var serviceObject;//服务层对象
		if($scope.entity.goods.id!=null){//如果有ID
			serviceObject=goodsService.update( $scope.entity ); //修改  
		}else{
			serviceObject=goodsService.add( $scope.entity  );//增加 
		}
        serviceObject.success(
			function(response){
				if(response.status){
					//重新查询 
		        	/*$scope.reloadList();//重新加载*/
					$scope.entity={goods:{},goodsDesc:{},itemList:[]};
                    editor.html('');//清空富文本编辑器
					location.href="goods.html";
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
				if(response.status){
					$scope.reloadList();//刷新列表
					$scope.selectIds=[];
				}	else {
					alert(data.msg)
				}
			}		
		);				
	}
	//审核商品
	$scope.commitGoods=function () {
		goodsService.commitGoods($scope.selectIds).success(function (data) {
            if(data.status){
                $scope.reloadList();//刷新列表
                $scope.selectIds=[];
            }else {
                alert(data.msg)
            }
        })
    }

	
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
		//$scope.entity.goods.category2Id=-1;
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
	//根据摸版id查询typeTemplate表下面的品牌列表
	findBrandListByTypeId=function () {
		//console.log('findBrandListByTypeId执行了')
		//console.log($location.search(['id']))
        typeTemplateService.findOne($scope.entity.goods.typeTemplateId).success(function (data) {
            $scope.typeTemplate.BrandList=JSON.parse(data.brandIds);

                $scope.entity.goodsDesc.customAttributeItems=JSON.parse(data.customAttributeItems);


            //$scope.typeTemplate.specIds=JSON.parse(data.specIds);

           // console.log(111)
           // console.log($scope.typeTemplate.BrandList);
            //console.log($scope.entity.goodsDesc.customAttributeItems);

        })

    }
	//监控摸版id的变化，然后调用查询品牌列表的方法
	$scope.$watch('entity.goods.typeTemplateId',function (newValue,oldValue) {
		console.log('方法执行了')
		if (newValue){
            findBrandListByTypeId();
            $scope.findSpecList(newValue);
		}
    })

	//根据摸版id查询specList
	$scope.findSpecList=function (id) {
		typeTemplateService.findSpecList(id).success(function (data) {
            $scope.specList=data;
            // console.log($scope.specList);
        })
    }
	//根据网页中规格选项中的选中项生成$scope.entity.goodsDesc.specificationItems:[]对象
	//specificationItems:[]内部属性:{attributeName:"",attributeValue:[]}

	/*
	* 该方法根据传入字符串判断该集合对象中是否有属性值为该字符串的子对象
	*
	* */
	getObjectByName=function (list,attrName,name) {
		var i=0,
			lenth=list.length;
		for (i;i<lenth;i++){
			if (list[i][attrName]===name){
				return list[i];
			}
		}
		return null;

    }

	$scope.updateSpecificationItems=function (e,name,value) {
		var list=getObjectByName($scope.entity.goodsDesc.specificationItems,'attributeName',name);

		if(list!=null){
			if (e.target.checked){  //如果该项被选中，则向attributeValue数组中增加该值
				list.attributeValue.push(value);
			}else {		//如果该项被取消选中，则向attributeValue数组中删除该值
				var index=list.attributeValue.indexOf(value);
				list.attributeValue.splice(index,1);
				if (list.attributeValue.length===0){
                    index=$scope.entity.goodsDesc.specificationItems.indexOf(list);
                    $scope.entity.goodsDesc.specificationItems.splice(index,1);
				}
			}


		}else { //如当前字段的对象尚未被创建，则新增该对象
            $scope.entity.goodsDesc.specificationItems.push({'attributeName':name,'attributeValue':[value]})
		}
    }


    $scope.createItemList=function(){
        $scope.entity.itemList=[{spec:{},price:0,num:99999,status:'0',isDefault:'0' } ]
        ;//初始
        var items=  $scope.entity.goodsDesc.specificationItems;

        for(var i=0;i< items.length;i++){
            $scope.entity.itemList =

            addColumn( $scope.entity.itemList,items[i].attributeName,items[i].attributeValue );
        }
    }
//添加列值
    addColumn=function(list,columnName,conlumnValues){
        var newList=[];//新的集合
        for(var i=0;i<list.length;i++){
            var oldRow= list[i];
            for(var j=0;j<conlumnValues.length;j++){
                var newRow= JSON.parse( JSON.stringify( oldRow )  );//深克隆
                newRow.spec[columnName]=conlumnValues[j];
                newList.push(newRow);
            }
        }
        return newList;
    }

    $scope.findOneById=function(id){
        /*var id=$location.search()['id'];
        console.log(id);*/
        if (id){
            goodsService.findOneById(id).success(
                function(response){
                    $scope.entity= response;
                    editor.html(response.goodsDesc.introduction);
                   // console.log(response.itemList)
                    $scope.entity.goodsDesc.itemImages=JSON.parse(response.goodsDesc.itemImages);
                    $scope.entity.goodsDesc.customAttributeItems=JSON.parse(response.goodsDesc.customAttributeItems);
                    $scope.entity.goodsDesc.specificationItems=JSON.parse(response.goodsDesc.specificationItems);
					//console.log($scope.entity.goodsDesc.specificationItems);

					for(var i=0;i<response.itemList.length;i++){
                        $scope.entity.itemList[i].spec=JSON.parse(response.itemList[i]['spec']);

					}
                  //  console.log($scope.entity.itemList.spec);

                }
            );
        }
    }
    
    //判断规格选项的选中状态,被选中返回true，否则返回false
	$scope.checkedSpec=function (specName,optionName) {
		var specList=$scope.entity.goodsDesc.specificationItems;
		var i=0,
			length=specList.length;
		for (i;i<length;i++){
			if (specList[i]['attributeName']===specName){
				if(specList[i]['attributeValue'].indexOf(optionName)>=0){
					return true;
				}
			}
		}
		return false;
    }
    
    //获取所有分类信息
	$scope.findAllItemCat=function () {
		console.log("findAllItemCat执行了");
		itemCatService.findAll().success(function (data) {
			var i=0,
				length=data.length;
			for(i;i<length;i++){
                $scope.itemCatList[data[i].id]=data[i].name;
               //console.log($scope.itemCatList[i])
			}
			//console.log($scope.itemCatList)
        })
    }
});	
