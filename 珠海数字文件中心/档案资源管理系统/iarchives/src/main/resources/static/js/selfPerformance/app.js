/**
 * Created by Administrator on 2020/4/13.
 */


Ext.Loader.setConfig({
    disableCaching : false
});
Ext.application({
    requires : [ 'Ext.container.Viewport' ],

    name : 'SelfPerformance', // 定义的命名空间
    appFolder : '../js/selfPerformance', // 指明应用的根目录

    controllers : ['SelfPerformanceController'],

    launch : function() {
        Ext.create('Ext.container.Viewport', {
            layout : 'fit',
            items : {
                xtype : 'selfPerformanceGridView'
            }
        });
    }
});
