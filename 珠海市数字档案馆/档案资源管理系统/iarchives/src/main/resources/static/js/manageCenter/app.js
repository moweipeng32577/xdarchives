/**
 * Created by Administrator on 2020/7/21.
 */


Ext.Loader.setConfig({
    disableCaching : false
});
Ext.application({
    requires : [ 'Ext.container.Viewport' ],

    name : 'ManageCenter', // 定义的命名空间
    appFolder : '../js/manageCenter', // 指明应用的根目录

    controllers : ['ManageCenterController'],

    launch : function() {
        Ext.create('Ext.container.Viewport', {
            layout : 'fit',
            items : {
                xtype : 'manageCenterView'
            }
        });
    }
});
