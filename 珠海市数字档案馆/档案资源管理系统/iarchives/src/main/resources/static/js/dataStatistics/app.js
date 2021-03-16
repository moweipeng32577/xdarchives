/**
 * Created by Administrator on 2019/6/11.
 */
Ext.Loader.setConfig({
    disableCaching : false
});
Ext.application({
    requires : [ 'Ext.container.Viewport' ],

    name : 'DataStatistics', // 定义的命名空间
    appFolder : '../js/dataStatistics', // 指明应用的根目录

    controllers : ['ReportSearchController'],

    launch : function() {
        Ext.create('Ext.container.Viewport', {
            layout : 'fit',
            items : {
                xtype : 'ReportSearchView'
            }
        });
    }
});