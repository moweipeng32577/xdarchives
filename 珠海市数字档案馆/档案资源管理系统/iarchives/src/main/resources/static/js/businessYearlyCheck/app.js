/**
 * Created by Administrator on 2020/10/13.
 */


Ext.Loader.setConfig({
    disableCaching : false
});
Ext.application({
    requires : [ 'Ext.container.Viewport' ],

    name : 'BusinessYearlyCheck', // 定义的命名空间
    appFolder : '../js/businessYearlyCheck', // 指明应用的根目录

    controllers : ['BusinessYearlyCheckController'],

    launch : function() {
        Ext.create('Ext.container.Viewport', {
            layout : 'fit',
            items : {
                xtype : 'businessYearlyCheckView'
            }
        });
    }
});
