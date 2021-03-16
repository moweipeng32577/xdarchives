/**
 * Created by Administrator on 2020/4/27.
 */


Ext.Loader.setConfig({
    disableCaching : false
});
Ext.application({
    requires : [ 'Ext.container.Viewport' ],

    name : 'MyOrder', // 定义的命名空间
    appFolder : '../js/myOrder', // 指明应用的根目录

    controllers : ['MyOrderController'],

    launch : function() {
        Ext.create('Ext.container.Viewport', {
            layout : 'fit',
            items : {
                xtype : 'myOrderGridView'
            }
        });
    }
});
