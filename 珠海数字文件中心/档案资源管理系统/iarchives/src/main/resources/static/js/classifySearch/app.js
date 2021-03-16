/**
 * Created by RonJiang on 2017/10/26 0026.
 */
Ext.Loader.setConfig({
    disableCaching : false
});
Ext.application({
    requires : [ 'Ext.container.Viewport' ],

    name : 'ClassifySearch', // 定义的命名空间
    appFolder : '../js/classifySearch', // 指明应用的根目录

    controllers : [ 'ClassifySearchController' ],

    launch : function() {
        Ext.create('Ext.container.Viewport', {
            layout : 'fit',
            items : {
                xtype : 'classifySearchView'
            }
        });
    }
});