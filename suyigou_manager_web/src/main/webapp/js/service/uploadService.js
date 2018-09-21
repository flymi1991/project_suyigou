app.service('uploadService', function ($http) {
    this.uploadFile = function () {
        var formData = new FormData();
        formData.append('file', file.files[0]);
        return $http({
            method: 'POST',
            url: "../upload.do",
            data: formData,
            headers: {'Content-Type': undefined},//让浏览器帮我们把 Content-Type 设置为 multipart/form-data
            transformRequest: angular.identity //angular transformRequest function 将表单数据序列化
        })
    }
})