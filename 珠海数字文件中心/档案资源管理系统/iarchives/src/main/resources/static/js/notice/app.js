Ext.Loader.setConfig({
    disableCaching : false
});
Ext.application({
    requires : [ 'Ext.container.Viewport' ],

    name : 'Notice', // 定义的命名空间
    appFolder : '../js/notice', // 指明应用的根目录

    controllers : ['NoticeController'],

    launch : function() {
        Ext.create('Ext.container.Viewport', {
            layout : 'fit',
            items : {
                xtype : 'noticeGridView'
            }
        });
    }
});