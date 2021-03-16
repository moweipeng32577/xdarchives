Ext.Loader.setConfig({
    disableCaching : false
});
Ext.application({
    requires : [ 'Ext.container.Viewport' ],

    name : 'MetadataAdmin', // 定义的命名空间
    appFolder : '../js/metadataAdmin', // 指明应用的根目录

    controllers : ['MetadataAdminController'],

    launch : function() {
        Ext.create('Ext.container.Viewport', {
            layout : 'fit',
            items : {
                xtype : 'MetadataAdminView'
            }
        });
    }
});