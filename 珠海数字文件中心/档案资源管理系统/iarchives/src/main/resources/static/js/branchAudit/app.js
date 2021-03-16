/**
 * Created by Administrator on 2020/9/17.
 */
Ext.Loader.setConfig({
    disableCaching : false
});
Ext.application({
    requires : [ 'Ext.container.Viewport' ],

    name : 'BranchAudit', // 定义的命名空间
    appFolder : '../js/branchAudit', // 指明应用的根目录

    controllers : ['BranchAuditController'],

    launch : function() {
        Ext.create('Ext.container.Viewport', {
            layout : 'fit',
            items : {
                xtype : 'branchAuditView'
            }
        });
    }
});
