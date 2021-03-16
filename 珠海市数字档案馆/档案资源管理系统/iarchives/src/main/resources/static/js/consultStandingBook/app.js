/**
 * Created by Leo on 2020/7/3 0003.
 */

Ext.Loader.setConfig({
    disableCaching : false
});
Ext.application({
    requires : [ 'Ext.container.Viewport' ],
    name : 'ConsultStandingBook', // 定义的命名空间
    appFolder : '../js/consultStandingBook', // 指明应用的根目录
    controllers : ['ConsultStandingBookController'],
    launch : function() {
        Ext.create('Ext.container.Viewport', {
            layout : 'fit',
            items : {
                xtype : 'statisticsGridView'
            }
        });
    }
});