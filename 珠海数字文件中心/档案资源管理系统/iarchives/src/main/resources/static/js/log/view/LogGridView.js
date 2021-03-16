/**
 * Created by xd on 2017/10/21.
 */
Ext.define('Log.view.LogGridView', {
    extend: 'Comps.view.BasicGridView',
    xtype: 'logGridView',
    itemId:'logGridViewID',
    region: 'center',
    allowDrag:true,
    searchstore: [
        {item: "operate_user", name: "操作人"},
        {item: "realname", name: "用户名"},
        {item: "organ", name: "机构"},
        {item: "module", name: "模块"},
        {item: "ip", name: "ip地址"},
        {item: "startTime", name: "操作时间"},
        {item: "desci", name: "操作描述"}
    ],
    tbar: functionButton,
    store: 'LogGridStore',
    columns: [
        {text: 'IP地址', dataIndex: 'ip', flex: 2, menuDisabled: true},
        {text: '操作人', dataIndex: 'operate_user', flex: 2, menuDisabled: true},
        {text: '用户名', dataIndex: 'realname', flex: 2, menuDisabled: true},
        {text: '机构', dataIndex: 'organ', flex: 2, menuDisabled: true},
        {text: '模块', dataIndex: 'module', flex: 2, menuDisabled: true},
        {text: '操作时间', dataIndex: 'startTime', flex: 2, menuDisabled: true},
        {text: '操作描述', dataIndex: 'desci', flex: 3, menuDisabled: true}
    ],
    hasSelectAllBox:true
});
