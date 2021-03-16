/**
 * Created by xd on 2017/10/21.
 */
Ext.Loader.setConfig({
    disableCaching : false
});
Ext.application({
    requires : [ 'Ext.container.Viewport' ],

    name : 'ArchivesMigrate', // 定义的命名空间
    appFolder : '../js/archivesMigrate', // 指明应用的根目录

    controllers : ['ArchivesMigrateController'],

    launch : function() {
        Ext.create('Ext.container.Viewport', {
            layout : 'fit',
            items : {
                xtype : 'archivesMigrateView'
            }
        });
    }
});