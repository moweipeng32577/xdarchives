/**
 * Created by huamx on 2018/03/14
 */
Ext.define('Userimg.controller.UserimgController', {
    extend: 'Ext.app.Controller',
    views: ['UploadUserimgView'],
    init:function () {
        this.control({
            'UploadUserimgView':{
                render:function () {
                    var path=window.parent.personalizedObject.path;
                    var mediaFrame = document.getElementById('mediaFrame');
                    if(path) {
                        mediaFrame.setAttribute('src', '/electronic/showUserimg');
                    }else{
                        var ifrmBody= mediaFrame.contentWindow.document.body;
                        var img = document.createElement("img");
                        img.src = '/img/user_default.png';
                        img.setAttribute('width','360px');
                        img.style.cssText = 'position: relative;left:-10px;top:-10px;';
                        ifrmBody.appendChild(img);
                    }
                }
            }
        });
    }
});