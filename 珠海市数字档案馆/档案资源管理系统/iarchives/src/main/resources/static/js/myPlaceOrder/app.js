/**
 * Created by Administrator on 2020/6/11.
 */


Ext.Loader.setConfig({
    disableCaching : false
});
Ext.application({
    requires : [ 'Ext.container.Viewport' ],

    name : 'MyPlaceOrder', // 定义的命名空间
    appFolder : '../js/myPlaceOrder', // 指明应用的根目录

    controllers : ['MyPlaceOrderController'],

    launch : function() {
        Ext.create('Ext.container.Viewport', {
            layout : 'fit',
            items : {
                xtype : 'myOrderGridView'
            }
        });
    }
});
