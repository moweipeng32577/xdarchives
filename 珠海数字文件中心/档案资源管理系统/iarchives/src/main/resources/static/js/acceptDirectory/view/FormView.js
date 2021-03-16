/**
 * Created by Administrator on 2019/6/24.
 */


Ext.define('AcceptDirectory.view.FormView',{
    extend:'Ext.panel.Panel',
    xtype:'formView',
    layout:'fit',
    items:[{
        itemId:'northform',//上方的表单视图
        xtype:'acceptDirectoryFormView'//表单类型
    }]
});
