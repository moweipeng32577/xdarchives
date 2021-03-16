/**
 * Created by RonJiang on 2018/04/22
 */
Ext.Loader.setConfig({
    disableCaching : false
});
Ext.application({
    requires : [ 'Ext.container.Viewport' ],

    name : 'Recyclebin', // 定义的命名空间
    appFolder : '../js/recyclebin', // 指明应用的根目录

    controllers : ['RecyclebinController'],

    launch : function() {
        Ext.create('Ext.container.Viewport', {
            layout : 'fit',
            items : {
                xtype : 'recyclebin'
            }
        });
    }
});