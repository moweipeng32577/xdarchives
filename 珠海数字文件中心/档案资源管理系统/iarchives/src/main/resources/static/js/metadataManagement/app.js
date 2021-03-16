/**
 * Created by tanly on 2017/10/24 0024.
 */
Ext.Loader.setConfig({
    disableCaching : false
});
Ext.application({
    requires : [ 'Ext.container.Viewport' ],

    name : 'MetadataManagement', // 定义的命名空间
    appFolder : '../js/metadataManagement', // 指明应用的根目录

    controllers : ['MetadataManagementController'],

    launch : function() {
        Ext.create('Ext.container.Viewport', {
            layout : 'fit',
            items : {
                xtype : 'metadataManagementFormAndGrid'
            }
        });
    }
});