/**
    * Created by RonJiang on 2017/10/24 0024.
    */
Ext.Loader.setConfig({
    disableCaching : false
});
Ext.application({
    requires : [ 'Ext.container.Viewport' ],

    name : 'SimpleSearch', // 定义的命名空间
    appFolder : '../js/simpleSearch', // 指明应用的根目录

    controllers : [ 'SimpleSearchController'],

    launch : function() {
        Ext.create('Ext.container.Viewport', {
            layout : 'fit',
            items : {
                xtype : 'simpleSearchView'
            }
        });
    }
});