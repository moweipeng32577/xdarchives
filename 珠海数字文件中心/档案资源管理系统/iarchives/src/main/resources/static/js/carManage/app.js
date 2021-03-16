/**
 * Created by Administrator on 2020/4/17.
 */


Ext.Loader.setConfig({
    disableCaching : false
});
Ext.application({
    requires : [ 'Ext.container.Viewport' ],

    name : 'CarManage', // 定义的命名空间
    appFolder : '../js/carManage', // 指明应用的根目录

    controllers : ['CarManageController'],

    launch : function() {
        Ext.create('Ext.container.Viewport', {
            layout : 'fit',
            items : {
                xtype : 'carManageView'
            }
        });
    }
});
