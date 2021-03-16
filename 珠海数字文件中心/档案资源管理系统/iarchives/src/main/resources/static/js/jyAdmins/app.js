/**
 * Created by xd on 2017/10/21.
 */
Ext.Loader.setConfig({
    disableCaching : false
});
Ext.application({
    requires : [ 'Ext.container.Viewport' ],

    name : 'JyAdmins', // 定义的命名空间
    appFolder : '../js/jyAdmins', // 指明应用的根目录

    controllers : ['JyAdminsController'],

    launch : function() {
        Ext.create('Ext.container.Viewport', {
            layout : 'fit',
            items : {
                xtype : 'jyAdminsView'
            }
        });
    }
});