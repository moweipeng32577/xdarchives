/**
 * Created by Administrator on 2019/6/26.
 */


Ext.Loader.setConfig({
    disableCaching : false
});
Ext.application({
    requires : [ 'Ext.container.Viewport' ],

    name : 'SimpleSearchDirectory', // 定义的命名空间
    appFolder : '../js/simpleSearchDirectory', // 指明应用的根目录

    controllers : [ 'SimpleSearchDirectoryController'],

    launch : function() {
        Ext.create('Ext.container.Viewport', {
            layout : 'fit',
            items : {
                xtype : 'simpleSearchDirectoryView'
            }
        });
    }
});
