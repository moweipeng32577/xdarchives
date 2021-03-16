/**
 * Created by huamx on 2018/03/14
 */
Ext.Loader.setConfig({
    disableCaching : false
});
Ext.application({
    name : 'Userimg', // 定义的命名空间
    appFolder : '../js/userimg', // 指明应用的根目录
    controllers : ['UserimgController'],
    requires : [ 'Ext.container.Viewport' ],
    launch : function() {
        Ext.create('Ext.container.Viewport', {
            layout : 'fit',
            items : {
                xtype :'UploadUserimgView'
            }
        });
    }
});