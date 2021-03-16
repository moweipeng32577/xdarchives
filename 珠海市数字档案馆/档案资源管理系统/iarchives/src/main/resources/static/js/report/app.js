/**
 * Created by RonJiang on 2018/02/27
 */
Ext.Loader.setConfig({
    disableCaching : false
});
Ext.application({
    requires : [ 'Ext.container.Viewport' ],

    name : 'Report', // 定义的命名空间
    appFolder : '../js/report', // 指明应用的根目录

    controllers : ['ReportController'],

    launch : function() {
        Ext.create('Ext.container.Viewport', {
            layout : 'fit',
            items : {
                xtype : 'report'
            }
        });
    }
});