app.service("brandService",function ($http) {
    //查询所有品牌
    this.findBrand=function () {
        return $http.get("../brand/findAll.do");
    }
    //分页查询所有品牌
    this.findPage=function (page,rows) {
        return $http.post("../brand/findPage.do?page="+page+"&rows="+rows);
    }
    //修改或增加品牌
    this.insert=function (entity) {
        return $http.post("../brand/insertBrand.do",entity);
    }

    this.update=function (entity) {
        return $http.post("../brand/updateBrand.do",entity);
    }

    //根据id查询品牌
    this.findOne=function (id) {

        return $http.get("../brand/findOne.do?id="+id);
    }
    //批量删除品牌
    this.dele=function (selectIds) {
        return $http.post("../brand/deleteBrands.do",selectIds);
    }

    //按条件分页查询
    this.search=function (page,rows,searchEntity) {
        return $http.post("../brand/search.do?page="+page+"&size="+rows,searchEntity);
    }

    this.selectOptionList=function () {
        return $http.post("../brand/selectOptionList.do");
    }
});