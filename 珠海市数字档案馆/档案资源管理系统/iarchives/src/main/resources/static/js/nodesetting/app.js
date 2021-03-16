/**
 * Created by tanly on 2017/10/24 0024.
 */
Ext.Loader.setConfig({
    disableCaching : false
});
Ext.application({
    requires : [ 'Ext.container.Viewport' ],

    name : 'Nodesetting', // 定义的命名空间
    appFolder : '../js/nodesetting', // 指明应用的根目录

    controllers : ['NodesettingController'],

    launch : function() {
        Ext.create('Ext.container.Viewport', {
            layout : 'fit',
            items : {
                xtype : 'nodesettingView'
            }
        });
    }
});