/**
 * Created by Administrator on 2019/6/11.
 */
Ext.Loader.setConfig({
    disableCaching : false
});
Ext.application({
    requires : [ 'Ext.container.Viewport' ],

    name : 'ReportManage', // 定义的命名空间
    appFolder : '../js/storeroom/reportManage', // 指明应用的根目录

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