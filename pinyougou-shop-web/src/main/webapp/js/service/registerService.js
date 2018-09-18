app.service("registerService",function ($http) {
    this.register=function (entity) {
       return $http.post("../seller/register.do",entity);
    }
})