app.service("loginService",function ($http) {
    this.login=function () {
        return $http.post("../login/name.do");
    }
})