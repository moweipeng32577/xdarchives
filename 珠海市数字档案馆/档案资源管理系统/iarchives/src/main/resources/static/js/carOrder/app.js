/**
 * Created by Administrator on 2020/4/21.
 */


Ext.Loader.setConfig({
    disableCaching : false
});
Ext.application({
    requires : [ 'Ext.container.Viewport' ],

    name : 'CarOrder', // 定义的命名空间
    appFolder : '../js/carOrder', // 指明应用的根目录

    controllers : ['CarOrderController'],

    launch : function() {
        Ext.create('Ext.container.Viewport', {
            layout : 'fit',
            items : {
                xtype : 'carOrderAdminView'
            }
        });
    }
});
