/**
 * Created by Administrator on 2019/6/25.
 */


Ext.define('ManageDirectory.view.FormAndGridView',{
    extend:'Ext.panel.Panel',
    xtype:'formAndGrid',
    layout:'border',
    items:[{
        region:'center',//中间
        flex:2,//占三分之二
        itemId:'northform',//上方的表单视图
        xtype:'manageDirectoryFormView'//表单类型
    },{
        region:'south',//南部
        flex:1,//占三分之一
        itemId:'southviewgrid',//下方的表格视图
        xtype:'manageDirectoryGridView',//表格类型
        split:true,
        collapsible:true,
        collapseToolText:'收起',
        expandToolText:'展开'
    }]
});
