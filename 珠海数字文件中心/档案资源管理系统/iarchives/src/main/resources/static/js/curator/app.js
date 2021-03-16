/**
 * Created by Administrator on 2020/7/20.
 */

Ext.Loader.setConfig({
    disableCaching : false
});
Ext.application({
    requires : [ 'Ext.container.Viewport' ],

    name : 'Curator', // 定义的命名空间
    appFolder : '../js/curator', // 指明应用的根目录

    controllers : ['CuratorController'],

    launch : function() {
        Ext.create('Ext.container.Viewport', {
            layout : 'fit',
            items : {
                xtype : 'curatorView'
            }
        });
    }
});