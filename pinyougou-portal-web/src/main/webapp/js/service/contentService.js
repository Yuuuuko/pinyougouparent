app.service("contentService",function ($http) {
    //按分类id查询广告列表
    this.findByCategoryId=function (categoryId) {
        return $http.get('../content/findByCategoryId.do?categoryId='+categoryId);
    }

    //查询所有广告列表
    this.findAll=function () {
        return $http.post('../content/findAllContent.do');
    }



});