/**
 * Created by RonJiang on 2018/05/09.
 */
Ext.Loader.setConfig({
    disableCaching : false
});
Ext.application({
    requires : [ 'Ext.container.Viewport' ],

    name : 'ArchiveManagement', // 定义的命名空间
    appFolder : '../js/archiveManagement', // 指明应用的根目录

    controllers : ['ArchiveController'],

    launch : function() {
        Ext.create('Ext.container.Viewport', {
            layout : 'fit',
            items : {
                xtype : 'archiveView'
            }
        });
    }
});