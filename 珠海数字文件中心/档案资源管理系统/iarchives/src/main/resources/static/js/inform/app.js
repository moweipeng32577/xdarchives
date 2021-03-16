/**
 * Created by xd on 2017/10/21.
 */
Ext.Loader.setConfig({
    disableCaching : false
});
Ext.application({
    requires : [ 'Ext.container.Viewport' ],

    name : 'Inform', // 定义的命名空间
    appFolder : '../js/inform', // 指明应用的根目录

    controllers : ['InformController'],

    launch : function() {
        Ext.create('Ext.container.Viewport', {
            layout : 'fit',
            items : {
                xtype : 'informGridView'
            }
        });
    }
});