/**
 * Created by Administrator on 2019/6/25.
 */



Ext.Loader.setConfig({
    disableCaching : false
});
Ext.application({
    requires : [ 'Ext.container.Viewport' ],

    name : 'ManageDirectory', // 定义的命名空间
    appFolder : '../js/manageDirectory', // 指明应用的根目录

    controllers : ['ManageDirectoryController'],

    launch : function() {
        Ext.create('Ext.container.Viewport', {
            layout : 'fit',
            items : {
                xtype : 'manageDirectoryFormAndGridView'//修改成表单与表格视图
            }
        });
    }
});
