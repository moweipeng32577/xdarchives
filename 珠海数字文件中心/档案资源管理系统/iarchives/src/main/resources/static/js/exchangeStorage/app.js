/**
 * Created by yl on 2017/10/25.
 */
Ext.Loader.setConfig({
    disableCaching : false
});
Ext.application({
    requires : [ 'Ext.container.Viewport' ],

    name : 'ExchangeStorage', // 定义的命名空间
    appFolder : '../js/exchangeStorage', // 指明应用的根目录

    controllers : ['ExchangeStorageController'],

    launch : function() {
        Ext.Ajax.request({
            url: '/exchangeStorage/clearSip',
            method: 'POST',
            sync: true,
            success : function(response,opts) {
            },
            failure:function(response,opts) {
            }
        });
        Ext.create('Ext.container.Viewport', {
            layout : 'fit',
            items : {
                xtype : 'exchangeStorageView'
            }
        });
    }
});