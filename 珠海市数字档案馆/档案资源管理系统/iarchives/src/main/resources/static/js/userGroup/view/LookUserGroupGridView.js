/**
 * Created by Administrator on 2019/12/14.
 */


Ext.define('UserGroup.view.LookUserGroupGridView',{
    extend:'Comps.view.BasicGridView',
    xtype:'lookUserGroupGridView',
    itemId:'lookUserGroupGridViewId',
    hasSearchBar:false,
    allowDrag:true,
    tbar: [{
        itemId: 'addUserOnGroup',
        xtype: 'button',
        text: '设置用户',
        hidden: userLoginname=='aqbm'? false:true  //aqbm账号有设置用户组用户功能
    },{
        itemId: 'delUserOnGroup',
        xtype: 'button',
        text: '删除',
        hidden: userLoginname=='aqbm'? false:true //aqbm账号有增加用户组用户功能
    },{
        itemId: 'closeUserOnGroup',
        xtype: 'button',
        text: '返回'
    }],
    store: 'LookUserGroupGridStore',
    columns: [
        {text: '帐号', dataIndex: 'loginname', flex: 2, menuDisabled: true},
        {text: '姓名', dataIndex: 'realname', flex: 2, menuDisabled: true},
        {text: '性别', dataIndex: 'sex', flex: 2, menuDisabled: true},
        {text: '所属机构', dataIndex: 'organname', flex: 2, menuDisabled: true},
        {text: '状态', dataIndex: 'status', flex: 2, menuDisabled: true},
        {text: '创建时间', dataIndex: 'createtime', flex: 2, menuDisabled: true},
        {text: '外来人员状态', dataIndex: 'outuserstate', flex: 2, menuDisabled: true}
    ]
});
