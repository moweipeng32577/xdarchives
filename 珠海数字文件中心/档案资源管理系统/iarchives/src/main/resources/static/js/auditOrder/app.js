/**
 * Created by Administrator on 2020/6/13.
 */


Ext.Loader.setConfig({
    disableCaching : false
});
Ext.application({
    requires : [ 'Ext.container.Viewport' ],

    name : 'AuditOrder', // 定义的命名空间
    appFolder : '../js/auditOrder', // 指明应用的根目录

    controllers : ['AuditOrderController'],

    launch : function() {
        Ext.create('Ext.container.Viewport', {
            layout : 'fit',
            items : {
                xtype : 'auditOrderAdminView'
            }
        });
    }
});
