/**
 * Created by Administrator on 2020/3/23.
 */


Ext.Loader.setConfig({
    disableCaching : false
});
Ext.application({
    requires : [ 'Ext.container.Viewport' ],

    name : 'AppraiseManage', // 定义的命名空间
    appFolder : '../js/appraiseManage', // 指明应用的根目录

    controllers : ['AppraiseManageController'],

    launch : function() {
        Ext.create('Ext.container.Viewport', {
            layout : 'fit',
            items : {
                xtype : 'appraiseManageGridView'
            }
        });
    }
});
