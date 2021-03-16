/**
 * Created by tanly on 2017/11/17.
 */
Ext.Loader.setConfig({
    disableCaching : false
});
Ext.application({
    requires : [ 'Ext.container.Viewport' ],

    name : 'FullSearch', // 定义的命名空间
    appFolder : '../js/fullSearch', // 指明应用的根目录

    controllers : ['FullSearchController'],

    launch : function() {
        Ext.create('Ext.container.Viewport', {
            layout : 'fit',
            items : {
                xtype : 'fullSearchView'
            }
        });
    }
});