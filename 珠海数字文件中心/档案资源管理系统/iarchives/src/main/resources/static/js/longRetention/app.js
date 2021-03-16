/**
 * Created by yl on 2017/10/25.
 */
Ext.Loader.setConfig({
    disableCaching : false
});
Ext.application({
    requires : [ 'Ext.container.Viewport' ],

    name : 'LongRetention', // 定义的命名空间
    appFolder : '../js/longRetention', // 指明应用的根目录

    controllers : ['LongRetentionController'],

    launch : function() {
        Ext.create('Ext.container.Viewport', {
            layout : 'fit',
            items : {
                xtype : 'longRetentionView'
            }
        });
    }
});