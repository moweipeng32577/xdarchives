/**
 * Created by tanly on 2017/11/8 0024.
 */
Ext.Loader.setConfig({
    disableCaching : false
});
Ext.application({
    requires : [ 'Ext.container.Viewport' ],

    name : 'Template', // 定义的命名空间
    appFolder : '../js/template', // 指明应用的根目录

    controllers : ['TemplateController'],

    launch : function() {
        Ext.create('Ext.container.Viewport', {
            layout : 'fit',
            items : {
                xtype : 'templateView'
            }
        });
    }
});