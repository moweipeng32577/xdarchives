/**
 * Created by Administrator on 2019/10/31.
 */


Ext.define('Management.view.LookBackCaptureDocGridView', {
    extend: 'Comps.view.BasicGridView',
    xtype:'lookBackCaptureDocGridView',
    hasCloseButton:false,
    searchstore: [
        {item: "backreason", name: "退回原因"},
        {item: "backorgan", name: "退回部门"},
        {item: "backtime", name: "退回时间"},
        {item: "backer", name: "退回人"}
    ],
    tbar: [{
        itemId:'lookEntry',
        xtype: 'button',
        text: '详细内容'
    },'-',{
            itemId:'lookBackId',
            xtype: 'button',
            text: '返回'
        }],
    store: 'LookBackCaptureDocGridStore',
    columns: [
        {text: '退回原因', dataIndex: 'backreason', flex: 2, menuDisabled: true},
        {text: '退回人', dataIndex: 'backer', flex: 2, menuDisabled: true},
        {text: '退回部门', dataIndex: 'backorgan', flex: 2, menuDisabled: true},
        {text: '退回时间', dataIndex: 'backtime', flex: 3, menuDisabled: true},
        {text: '退回数量', dataIndex: 'backcount', flex: 2, menuDisabled: true}
    ]
});
