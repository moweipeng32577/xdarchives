/**
 * Created by Administrator on 2018/11/30.
 */

Ext.define('CheckGroup.view.CheckGroupGridView',{
    extend:'Comps.view.BasicGridView',
    xtype:'checkGroupGridView',
    itemId:'checkGroupGridViewid',
    hasSearchBar:false,
    allowDrag:true,
    tbar:[{
        itemId:'checksave',
        xtype: 'button',
        iconCls:'fa fa-plus-circle',
        text: '增加'
    }, '-', {
        itemId:'checkmodify',
        xtype: 'button',
        iconCls:'fa fa-pencil-square-o',
        text: '修改'
    }, '-', {
        itemId:'checkdelete',
        xtype: 'button',
        iconCls:'fa fa-trash-o',
        text: '删除'
    }, '-', {
        itemId:'checkuser',
        xtype: 'button',
        iconCls:'fa fa-user-plus',
        text: '质检人员管理'
    }],
    store: 'CheckGroupGridStore',
    columns: [
        {text: '质检组名', dataIndex: 'groupname', flex: 2, menuDisabled: true},
        {text: '描述', dataIndex: 'desci', flex: 2, menuDisabled: true},
        {text: '类型', dataIndex: 'type', flex: 2, menuDisabled: true}
    ]
});
