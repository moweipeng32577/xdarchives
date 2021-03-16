Ext.Loader.setConfig({
    disableCaching : false
});
Ext.application({
    requires : [ 'Ext.container.Viewport' ],

    name : 'AssemblyAdmin', // 定义的命名空间
    appFolder : '../js/assemblyAdmin', // 指明应用的根目录

    controllers : ['AssemblyAdminController'],

    launch : function() {
        Ext.create('Ext.container.Viewport', {
            layout : 'fit',
            items : {
                xtype : 'AssemblyAdminView'
            }
        });
    }
});