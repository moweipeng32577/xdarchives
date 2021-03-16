/**
 * Created by Administrator on 2017/10/24 0024.
 */
Ext.define('UserGroup.view.UserGroupView',{
    extend:'Comps.view.BasicGridView',
    xtype:'userGroupView',
    itemId:'userGroupViewID',
    hasSearchBar:false,
    allowDrag:true,
    tbar: functionButton,
    store: 'UserGroupStore',
    columns: [
        {text: '用户组名', dataIndex: 'rolename', flex: 2, menuDisabled: true},
        {text: '描述', dataIndex: 'desciption', flex: 2, menuDisabled: true}
    ]
});