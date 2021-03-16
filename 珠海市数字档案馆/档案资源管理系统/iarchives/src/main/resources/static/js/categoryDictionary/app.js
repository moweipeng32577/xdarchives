Ext.Loader.setConfig({
    disableCaching : false
});
Ext.application({
    requires : [ 'Ext.container.Viewport' ],

    name : 'CategoryDictionary', // 定义的命名空间
    appFolder : '../js/categoryDictionary', // 指明应用的根目录

    controllers : ['CategoryDictionaryController'],

    launch : function() {
        Ext.create('Ext.container.Viewport', {
            layout : 'fit',
            items : {
                xtype : 'categoryDictionary'
            }
        });
    }
});