/**
 * Created by RonJiang on 2018/04/08
 */
Ext.Loader.setConfig({
    disableCaching : false
});
Ext.application({
    requires : [ 'Ext.container.Viewport' ],

    name : 'Funds', // 定义的命名空间
    appFolder : '../js/funds', // 指明应用的根目录

    controllers : ['FundsController'],

    launch : function() {
        Ext.create('Ext.container.Viewport', {
            layout : 'fit',
            items : {
                xtype : 'funds'
            }
        });
    }
});