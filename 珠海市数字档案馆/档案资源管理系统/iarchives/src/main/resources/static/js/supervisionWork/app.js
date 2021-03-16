/**
 * Created by Administrator on 2020/10/12.
 */


Ext.Loader.setConfig({
    disableCaching : false
});
Ext.application({
    requires : [ 'Ext.container.Viewport' ],

    name : 'SupervisionWork', // 定义的命名空间
    appFolder : '../js/supervisionWork', // 指明应用的根目录

    controllers : ['SupervisionWorkController'],

    launch : function() {
        Ext.create('Ext.container.Viewport', {
            layout : 'fit',
            items : {
                xtype : 'supervisionWorkView'
            }
        });
    }
});
