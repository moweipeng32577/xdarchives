/**
 * Created by Administrator on 2019/6/24.
 */


Ext.Loader.setConfig({
    disableCaching : false
});
Ext.application({
    requires : [ 'Ext.container.Viewport' ],

    name : 'AcceptDirectory', // 定义的命名空间
    appFolder : '../js/acceptDirectory', // 指明应用的根目录

    controllers : ['AcceptDirectoryController','ImportController','BatchModifyController'],

    launch : function() {
        Ext.create('Ext.container.Viewport', {
            layout : 'fit',
            items : {
                xtype : 'acceptDirectoryFormAndGridView'//修改成表单与表格视图
            }
        });
    }
});
