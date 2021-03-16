/**
 * Created by Administrator on 2019/5/23.
 */


Ext.Loader.setConfig({
    disableCaching : false
});
Ext.application({
    requires : [ 'Ext.container.Viewport' ],

    name : 'ElectronPrintApprove', // 定义的命名空间
    appFolder : '../js/electronPrintApprove', // 指明应用的根目录

    controllers : ['ElectronPrintApproveController'],

    launch : function() {
        Ext.create('Ext.container.Viewport', {
            layout : 'fit',
            items : {
                xtype : 'electronPrintApproveView'
            }
        });
    }
});
