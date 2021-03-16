/**
 * Created by Administrator on 2019/7/3.
 */


Ext.define('AssemblyAdmin.view.AssemblyAdminUserGridView',{
    extend: 'Comps.view.BasicGridView',
    xtype:'assemblyAdminUserGridView',
    itemId:'assemblyAdminUserGridViewId',
    bodyBorder: false,
    store: 'AssemblyAdminUserGridStore',
    hasCloseButton:false,
    head:false,
    hasSearchBar:false,
    tbar: [{
        itemId:'adduser',
        xtype: 'button',
        iconCls:'fa fa-plus-circle',
        text: '增加'
    },'-',{
        itemId:'deluser',
        xtype: 'button',
        iconCls:'fa fa-trash-o',
        text: '删除'
    },'-',{
        itemId:'backuser',
        xtype: 'button',
        iconCls:'fa fa-trash-o',
        text: '返回'
    }
    ],
    columns: [
        {text: '账号', dataIndex: 'loginname', flex: 2, menuDisabled: true},
        {text: '姓名', dataIndex: 'realname', flex: 2, menuDisabled: true},
        {text: '性别', dataIndex: 'sex', flex: 2, menuDisabled: true},
        {text: '所属机构', dataIndex: 'organ', flex: 2, menuDisabled: true,
            renderer: function (value) {
                return value['organname'];
            }}
    ]
});
