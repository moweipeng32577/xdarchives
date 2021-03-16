/**
 * Created by xd on 2017/10/21.
 */
Ext.define('ArchiveManagement.view.ArchiveGridView',{
    extend: 'Comps.view.BasicGridView',
    xtype:'archiveGridView',
    region: 'center',
    itemId:'archiveGridViewID',
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
//    tbar: functionButton,
    tbar: [{
    	itemId:'userRegister',
        xtype: 'button',
        text: '外来人员查档等级'
    }],
    store: 'ArchiveGridStore',
    columns: [
        {text: '帐号', dataIndex: 'loginname', flex: 2, menuDisabled: true},
        {text: '姓名', dataIndex: 'realname', flex: 2, menuDisabled: true},
        {text: '性别', dataIndex: 'sex', flex: 2, menuDisabled: true},
        {
            text: '所属机构',
            dataIndex: 'organ',
            flex: 2,
            menuDisabled: true,
            renderer: function (value) {
                return value['organname'];
            }
        },
        {text: '状态', dataIndex: 'status', flex: 2, menuDisabled: true},
        {text: '创建时间', dataIndex: 'createtime', flex: 2, menuDisabled: true},
        {text: '外来人员状态', dataIndex: 'outuserstate', flex: 2, menuDisabled: true},
        {text: '到期时间', dataIndex: 'exdate', flex: 2, menuDisabled: true}
    ]
});