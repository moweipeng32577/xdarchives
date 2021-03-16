/**
 * Created by yl on 2017/10/25.
 */
/*
Ext.Loader.setConfig({
    disableCaching : false
});
Ext.application({
    requires : [ 'Ext.container.Viewport' ],

    name : 'Import', // 定义的命名空间
    appFolder : '../js/import', // 指明应用的根目录

    controllers : ['ImportController'],

    launch : function() {
        Ext.create('Ext.container.Viewport', {
            layout : 'fit',
            items : {
                xtype : 'importFormAndGrid'
            }
        });
    }
});*/
Ext.Loader.setConfig({
    disableCaching : false
});
Ext.application({
    requires : [ 'Ext.container.Viewport' ],

    name : 'Import', // 定义的命名空间
    appFolder : '../js/import', // 指明应用的根目录

    controllers : ['ImportController'],


    launch : function() {
        Ext.create('Ext.container.Viewport', {
            layout : 'fit',
            items : {
                xtype : 'import'
            }
        });
    }
});
