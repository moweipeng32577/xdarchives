/**
 * Created by Administrator on 2019/6/25.
 */


Ext.define('ManageDirectory.view.FormAndInnerGridView',{
    extend:'Ext.panel.Panel',
    xtype:'formAndInnerGrid',
    layout:'border',
    items:[{
        region:'center',//中间
        flex:2,//占三分之二
        itemId:'innernorthform',//上方的表单视图
        xtype:'manageDirectoryFormView'//表单类型
    },{
        region:'south',//南部
        flex:1,//占三分之一
        itemId:'innersouthviewgrid',//下方的表格视图
        xtype:'manageDirectoryGridView',//表格类型
        split:true,
        collapsible:true,
        collapseToolText:'收起',
        expandToolText:'展开',
        split:true,
        allowDrag:true,
        hasSearchBar:false,
        tbar:[
        //     {
        //     text:'著录',
        //     itemId:'isave'
        // },'-',{
        //     text:'修改',
        //     itemId:'imodify'
        // },'-',{
        //     text:'删除',
        //     itemId:'idel'
        // },'-',{
        //     text:'查看',
        //     itemId:'ilook'
        // }
        ]
    }]
});
