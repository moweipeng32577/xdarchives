/**
 * Created by Administrator on 2018/11/28.
 */

Ext.Loader.setConfig({
    disableCaching : false
});
Ext.application({
    requires : [ 'Ext.container.Viewport' ],

    name : 'Borrowfinish', // 定义的命名空间
    appFolder : '../js/borrowfinish', // 指明应用的根目录

    controllers : ['BorrowFinishController'],

    launch : function() {
        Ext.create('Ext.container.Viewport', {
            layout : 'fit',
            items : {
                xtype : 'borrowFinishGridView'
            }
        });
    }
});
