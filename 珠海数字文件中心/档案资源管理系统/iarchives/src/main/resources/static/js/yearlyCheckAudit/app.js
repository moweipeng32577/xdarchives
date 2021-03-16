/**
 * Created by Administrator on 2020/10/15.
 */


Ext.Loader.setConfig({
    disableCaching : false
});
Ext.application({
    requires : [ 'Ext.container.Viewport' ],

    name : 'YearlyCheckAudit', // 定义的命名空间
    appFolder : '../js/yearlyCheckAudit', // 指明应用的根目录

    controllers : ['YearlyCheckAuditController'],

    launch : function() {
        Ext.create('Ext.container.Viewport', {
            layout : 'fit',
            items : {
                xtype : 'yearlyCheckAuditView'
            }
        });
    }
});
