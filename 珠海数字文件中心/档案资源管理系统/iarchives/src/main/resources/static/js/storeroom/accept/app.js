Ext.Loader.setConfig({
    disableCaching : false
});
Ext.application({
    requires : [ 'Ext.container.Viewport' ],

    name : 'Accept', // 定义的命名空间
    appFolder : '../js/storeroom/accept', // 指明应用的根目录

    controllers : ['AcceptController'],

    launch : function() {
        Ext.create('Ext.container.Viewport', {
            layout : 'fit',
            items : {
                xtype : 'AcceptView'
            }
        });
    }
});