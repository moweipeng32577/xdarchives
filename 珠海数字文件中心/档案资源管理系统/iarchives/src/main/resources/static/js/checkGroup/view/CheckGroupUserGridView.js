/**
 * Created by Administrator on 2018/12/1.
 */

Ext.define('CheckGroup.view.CheckGroupUserGridView',{
    extend: 'Comps.view.BasicGridView',
    xtype:'checkGroupUserGridView',
    region: 'center',
    itemId:'checkGroupUserGridViewid',
    hasSearchBar:false,
    tbar:[{
        itemId:'checkusersave',
        xtype: 'button',
        iconCls:'fa fa-plus-circle',
        text: '增加'
    }, '-', {
        itemId:'checkuserdelete',
        xtype: 'button',
        iconCls:'fa fa-trash-o',
        text: '删除'
    }, '-', {
        itemId:'checkuserback',
        xtype: 'button',
        text: '返回'
    }],
    store: 'CheckGroupUserGridStore',
    columns: [
        {text: '帐号', dataIndex: 'loginname', flex: 2, menuDisabled: true},
        {text: '姓名', dataIndex: 'realname', flex: 2, menuDisabled: true},
        {text: '性别', dataIndex: 'sex', flex: 2, menuDisabled: true},
        {text: '所属机构', dataIndex: 'organname', flex: 2, menuDisabled: true,},
        {text: '所属组名', dataIndex: 'groupname', flex: 2, menuDisabled: true}
    ]
});
