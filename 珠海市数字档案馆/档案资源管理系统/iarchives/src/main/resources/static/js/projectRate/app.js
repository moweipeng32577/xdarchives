/**
 * Created by Administrator on 2020/5/9.
 */


Ext.Loader.setConfig({
    disableCaching : false
});
Ext.application({
    requires : [ 'Ext.container.Viewport' ],

    name : 'ProjectRate', // 定义的命名空间
    appFolder : '../js/projectRate', // 指明应用的根目录

    controllers : ['ProjectRateController'],

    launch : function() {
        Ext.create('Ext.container.Viewport', {
            layout : 'fit',
            items : {
                xtype : 'projectRateView'
            }
        });
    }
});
