/**
 * Created by Administrator on 2020/4/28.
 */


Ext.Loader.setConfig({
    disableCaching : false
});
Ext.application({
    requires : [ 'Ext.container.Viewport' ],

    name : 'PlaceOrder', // 定义的命名空间
    appFolder : '../js/placeOrder', // 指明应用的根目录

    controllers : ['PlaceOrderController'],

    launch : function() {
        Ext.create('Ext.container.Viewport', {
            layout : 'fit',
            items : {
                xtype : 'placeOrderAdminView'
            }
        });
    }
});
