/**
 * Created by wujy on 2019/07/30
 */
Ext.Loader.setConfig({
    disableCaching : false
});
Ext.application({
    requires : [ 'Ext.container.Viewport' ],

    name : 'ReservoirArea', // 定义的命名空间
    appFolder : '../js/storeroom/reservoirArea', // 指明应用的根目录

    controllers : ['ReservoirAreaController'],

    launch : function() {
        Ext.create('Ext.container.Viewport', {
            layout : 'fit',
            items : {
                xtype : 'reservoirAreaView'
            }
        });
    }
});