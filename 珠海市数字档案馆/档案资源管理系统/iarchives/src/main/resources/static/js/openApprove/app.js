/**
 * Created by tanly on 2017/12/5.
 */
Ext.Loader.setConfig({
    disableCaching : false
});
Ext.application({
    requires : [ 'Ext.container.Viewport' ],

    name : 'OpenApprove', // 定义的命名空间
    appFolder : '../js/openApprove', // 指明应用的根目录

    controllers : ['OpenApproveController'],

    launch : function() {
        Ext.create('Ext.container.Viewport', {
            layout : 'fit',
            items : {
                xtype : 'openApproveView'
            }
        });
    }
});