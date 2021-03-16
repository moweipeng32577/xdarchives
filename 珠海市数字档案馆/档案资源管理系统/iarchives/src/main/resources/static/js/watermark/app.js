/**
 * Created by tanly on 2017/10/24 0024.
 */
Ext.application({
    requires : [ 'Ext.container.Viewport' ],

    name : 'Watermark', // 定义的命名空间
    appFolder : '../js/watermark', // 指明应用的根目录

    controllers : ['WatermarkController'],

    launch : function() {
        Ext.create('Ext.container.Viewport', {
            layout : 'fit',
            items : {
                xtype : 'WatermarkView'
            }
        });
    }
});