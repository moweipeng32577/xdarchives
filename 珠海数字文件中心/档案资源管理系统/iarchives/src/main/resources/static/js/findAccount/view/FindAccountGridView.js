/**
 * Created by xd on 2017/10/21.
 */
Ext.define('FindAccount.view.FindAccountGridView',{
    extend: 'Comps.view.BasicGridView',
    xtype:'findAccountGridView',
    region: 'center',
    itemId:'findAccountGridViewID',
    allowDrag:true,
    searchstore:[
        {item: "loginname", name: "帐号"},
        {item: "realname", name: "姓名"},
        {item:"organ", name: "所属机构"}
    ],
    searchColumnRenderer: function (v, searchstrs) {
        var reTag = /<(?:.|\s)*?>/g;
        var value;
        if (typeof(v) == "object") {
            value = v['organname'].replace(reTag, "");
        } else {
            value = v.replace(reTag, "");
        }
        var reg = new RegExp(searchstrs.join('|'), 'g');
        return value.replace(reg, function (match) {
            return '<span style="color:red">' + match + '</span>'
        });
    },
    tbar: [{
    	itemId: 'resetPW',
    	xtype: 'button',
    	iconCls: 'fa fa-unlock-alt',
    	text: '初始化密码'
    }, '-', {
    	itemId: 'endisable',
    	xtype: 'button',
    	iconCls: 'fa fa-dot-circle-o',
    	text: '启用/禁用'
    }, '-', {
        itemId: 'userDel',
        xtype: 'button',
        iconCls: 'fa fa-trash-o',
        text: '删除'
    }, '-', {
        itemId: 'userEdit',
        xtype: 'button',
        iconCls: 'fa fa-pencil-square-o',
        text: '修改'
    }, '-', {
        itemId: 'exdate',
        xtype: 'button',
        iconCls: 'fa fa-bars',
        text: '调整到期时间'
    }, '-' ,{
    	itemId: 'userRegister',
        xtype: 'button',
        iconCls: 'fa fa-bars',
        text: '外来人员查档登记'
    }, '-' ,{
        itemId: 'submiteleborrow',
        iconCls: 'fa fa-plus-circle',
        text: '查档申办单'
        // menu: [{
        //     itemId: 'submiteleborrow',
        //     text: '提交电子查档单',
        //     iconCls: 'fa fa-plus-circle',
        //     menu: null
        // }, {
        //     itemId: 'submitstborrow',
        //     text: '提交实体查档单',
        //     iconCls: 'fa fa-plus-circle',
        //     menu: null
        // }]
    }],
    store: 'FindAccountGridStore',
    columns: [
        {text: '帐号', dataIndex: 'loginname', flex: 2, menuDisabled: true},
        {text: '姓名', dataIndex: 'realname', flex: 2, menuDisabled: true},
        {text: '性别', dataIndex: 'sex', flex: 2, menuDisabled: true},
        {text: '所属机构', dataIndex: 'organ', flex: 2, menuDisabled: true},
        {text: '状态', dataIndex: 'status', flex: 2, menuDisabled: true},
        {text: '有效期', dataIndex: 'infodate', flex: 2, menuDisabled: true},
        {text: '创建时间', dataIndex: 'createtime', flex: 2, menuDisabled: true},
        {text: '外来人员状态', dataIndex: 'outuserstate', flex: 2, menuDisabled: true},
        {text: '到期时间', dataIndex: 'exdate', flex: 2, menuDisabled: true}
    ]
});