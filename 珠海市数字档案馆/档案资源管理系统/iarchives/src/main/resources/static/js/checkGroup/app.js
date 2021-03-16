/**
 * Created by Administrator on 2018/11/30.
 */

Ext.Loader.setConfig({
    disableCaching : false
});
Ext.application({
    requires : [ 'Ext.container.Viewport' ],

    name : 'CheckGroup', // 定义的命名空间
    appFolder : '../js/checkGroup', // 指明应用的根目录

    controllers : ['CheckGroupController'],

    launch : function() {
        Ext.create('Ext.container.Viewport', {
            layout : 'fit',
            items : {
                xtype : 'checkGroupGridView'
            }
        });
    }
});
