/**
 * Created by Administrator on 2019/10/25.
 */


Ext.Loader.setConfig({
    disableCaching : false
});
Ext.application({
    requires : [ 'Ext.container.Viewport' ],

    name : 'TransforAuditDeal', // 定义的命名空间
    appFolder : '../js/transforAuditDeal', // 指明应用的根目录

    controllers : ['TransforAuditDealController'],

    launch : function() {
        Ext.create('Ext.container.Viewport', {
            layout : 'fit',
            items : {
                xtype : 'tranforAuditDealView'
            }
        });
    }
});
