/**
 * Created by Administrator on 2018/11/8.
 */

Ext.Loader.setConfig({
    disableCaching : false
});
Ext.application({
    requires : [ 'Ext.container.Viewport' ],

    name : 'Thematicelectronic', // 定义的命名空间
    appFolder : '../js/thematicelectronic', // 指明应用的根目录

    controllers : ['ThematicElectronicController'],

    launch : function() {
        Ext.create('Ext.container.Viewport', {
            layout : 'fit',
            items : {
                xtype : 'thematicDetailGridView'
            }
        });
    }
});
