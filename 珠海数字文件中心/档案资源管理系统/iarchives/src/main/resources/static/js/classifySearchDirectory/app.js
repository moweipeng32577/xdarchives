/**
 * Created by Administrator on 2019/6/27.
 */


Ext.Loader.setConfig({
    disableCaching : false
});
Ext.application({
    requires : [ 'Ext.container.Viewport' ],

    name : 'ClassifySearchDirectory', // 定义的命名空间
    appFolder : '../js/classifySearchDirectory', // 指明应用的根目录

    controllers : [ 'ClassifySearchDirectoryController' ],

    launch : function() {
        Ext.create('Ext.container.Viewport', {
            layout : 'fit',
            items : {
                xtype : 'classifySearchDirectoryView'
            }
        });
    }
});
