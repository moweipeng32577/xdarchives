/**
 * Created by Administrator on 2019/6/25.
 */


Ext.define('ManageDirectory.view.FormView',{
    extend:'Ext.panel.Panel',
    xtype:'formView',
    layout:'fit',
    items:[{
        itemId:'northform',//上方的表单视图
        xtype:'manageDirectoryFormView'//表单类型
    }]
});
