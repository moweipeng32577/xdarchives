/**
 * Created by xd on 2017/10/21.
 */
Ext.Loader.setConfig({
    disableCaching : false
});
Ext.application({
    requires : [ 'Ext.container.Viewport' ],

    name : 'Log', // 定义的命名空间
    appFolder : '../js/log', // 指明应用的根目录

    controllers : ['LogController'],

    launch : function() {
        Ext.create('Ext.container.Viewport', {
            layout : 'fit',
            items : {
                xtype : 'logView'
            }
        });
    }
});