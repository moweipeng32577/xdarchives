/**
 * Created by Administrator on 2020/4/20.
 */


Ext.Loader.setConfig({
    disableCaching : false
});
Ext.application({
    requires : [ 'Ext.container.Viewport' ],

    name : 'PlaceManage', // 定义的命名空间
    appFolder : '../js/placeManage', // 指明应用的根目录

    controllers : ['PlaceManageController'],

    launch : function() {
        Ext.create('Ext.container.Viewport', {
            layout : 'fit',
            items : {
                xtype : 'placeManageView'
            }
        });
    }
});
