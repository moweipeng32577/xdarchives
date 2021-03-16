/**
 * Created by Administrator on 2020/7/20.
 */

Ext.Loader.setConfig({
    disableCaching : false
});
Ext.application({
    requires : [ 'Ext.container.Viewport' ],

    name : 'Affair', // 定义的命名空间
    appFolder : '../js/affair', // 指明应用的根目录

    controllers : ['AffairController'],

    launch : function() {
        Ext.create('Ext.container.Viewport', {
            layout : 'fit',
            items : {
                xtype : 'affairView'
            }
        });
    }
});