/**
 * Created by Administrator on 2020/7/8.
 */


Ext.Loader.setConfig({
    disableCaching : false
});
Ext.application({
    requires : [ 'Ext.container.Viewport' ],

    name : 'SupervisionGuidance', // 定义的命名空间
    appFolder : '../js/supervisionGuidance', // 指明应用的根目录

    controllers : ['SupervisionGuidanceController'],

    launch : function() {
        Ext.create('Ext.container.Viewport', {
            layout : 'fit',
            items : {
                xtype : 'supervisionGuidanceView'
            }
        });
    }
});
