/**
 * Created by yl on 2017/10/25.
 */
Ext.Loader.setConfig({
    disableCaching : false
});
Ext.application({
    requires : [ 'Ext.container.Viewport' ],

    name : 'ThematicUtilize', // 定义的命名空间
    appFolder : '../js/thematicMakeUtilize', // 指明应用的根目录

    controllers : ['ThematicUtilizeController'],

    launch : function() {
        Ext.create('Ext.container.Viewport', {
            layout : 'fit',
            items : {
                //xtype : 'thematicUtilizeView'
                xtype:'thematicUtilizeViews'
            }
        });
    }
});