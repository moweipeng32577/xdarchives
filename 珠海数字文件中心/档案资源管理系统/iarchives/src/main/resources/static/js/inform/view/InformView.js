/**
 * Created by Administrator on 2017/10/24 0024.
 */
Ext.define('Inform.view.InformView',{
    extend: 'Comps.view.BasicGridView',
    xtype:'informGridView',
    itemId:'informGridViewID',
    bodyBorder: false,
    store: 'InformGridStore',
    hasCloseButton:false,
    head:false,
    searchstore:[
        {item: "title", name: "标题"},
        {item: "text", name: "内容"},
        {item: "postedman", name: "发布人"}
        ],
    tbar: [{
        itemId:'informAdd',
        xtype: 'button',
        iconCls:'fa fa-plus-circle',
        text: '增加'
        },'-',{
        itemId:'informEdit',
        xtype: 'button',
        iconCls:'fa fa-pencil-square-o',
        text: '修改'
        },'-',{
        itemId:'informDel',
        xtype: 'button',
        iconCls:'fa fa-trash-o',
        text: '删除'
        },'-',{
        itemId:'informLook',
        xtype: 'button',
        iconCls:'fa fa-eye',
        text: '查看'
        },'-',{
        itemId:'postedUser',
        xtype: 'button',
        iconCls:'fa fa-user',
        text: '推送到用户'
        },'-',{
        itemId:'postedUserGroup',
        xtype: 'button',
        iconCls:'fa fa-users',
        text: '推送到用户组'
        },'-',{
        itemId:'informStick',
        xtype: 'button',
        iconCls:'fa fa-upload',
        text: '置顶'
        },'-',{
        itemId:'cancelStick',
        xtype: 'button',
        iconCls:'fa  fa-level-down',
        text: '取消置顶'
        }
    ],
    columns: [
        {text: '标题', dataIndex: 'title', flex: 2, menuDisabled: true},
        {text: '内容', dataIndex: 'text', flex: 4, menuDisabled: true,renderer: function(value, cellmeta, record) {
            var reTag = /<(?:.|\s)*?>/g;
            return value.replace(reTag,"");
        } },
        {text: '创建人', dataIndex: 'postedman', flex: 1, menuDisabled: true},
        {text: '发布时间', dataIndex: 'informdate', flex: 1.5, menuDisabled: true},
        {text: '截止时间', dataIndex: 'limitdate', flex: 1.5, menuDisabled: true},
        {text: '发布用户', dataIndex: 'posteduser', flex: 1, menuDisabled: true},
        {text: '发布用户组', dataIndex: 'postedusergroup', flex: 1, menuDisabled: true},
        {text: '置顶等级', dataIndex: 'stick', flex: 1, menuDisabled: true}
    ]
});