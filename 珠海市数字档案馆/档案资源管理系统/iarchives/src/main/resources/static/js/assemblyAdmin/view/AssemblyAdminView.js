/**
 * Created by Administrator on 2017/10/24 0024.
 */
Ext.define('AssemblyAdmin.view.AssemblyAdminView',{
    extend: 'Comps.view.BasicGridView',
    xtype:'AssemblyAdminView',
    itemId:'AssemblyAdminViewID',
    bodyBorder: false,
    store: 'AssemblyAdminGridStore',
    hasCloseButton:false,
    head:false,
    searchstore:[
        {item: "title", name: "流水线名"},
        {item: "createtime", name: "创建时间"},
        {item: "creator", name: "创建人"},
        ],
    tbar: [{
            itemId:'add',
            xtype: 'button',
            iconCls:'fa fa-plus-circle',
            text: '增加'
        },'-',{
            itemId:'edit',
            xtype: 'button',
            iconCls:'fa fa-pencil-square-o',
            text: '修改'
        },'-',{
            itemId:'del',
            xtype: 'button',
            iconCls:'fa fa-trash-o',
            text: '删除'
        },'-',{
            itemId:'flows',
            xtype: 'button',
            iconCls:'fa fa-bars',
            text: '环节配置'
        },'-',{
            itemId:'allot',
            xtype: 'button',
            iconCls:'fa fa-user-plus    ',
            text: '人员分配'
        }, '-',{
        itemId: 'adminuser',
        xtype: 'button',
        iconCls: 'fa fa-user',
        text: '管理员'
    }
    ],
    columns: [
        {text: '流水线名', dataIndex: 'title', flex: 2, menuDisabled: true},
        {text: '编码', dataIndex: 'code', flex: 1.5, menuDisabled: true},
        {text: '创建时间', dataIndex: 'createtime', flex: 1.5, menuDisabled: true},
        {text: '创建人', dataIndex: 'creator', flex: 1, menuDisabled: true}
    ]
});