/**
 * Created by Leo on 2019/04/25.
 */
Ext.Loader.setConfig({
    disableCaching : false
});
Ext.application({
    requires : [ 'Ext.container.Viewport' ],

    name : 'FileChecker', // 定义的命名空间,
    id:'FileChecker',
    appFolder : '../js/fileChecker', // 指明应用的根目录

    controllers : ['FileCheckerController'],

    launch : function() {
        Ext.create('Ext.container.Viewport', {
            layout : 'fit',
            items : {
                xtype : 'fileCheckerView'
            }
        });
    }
});