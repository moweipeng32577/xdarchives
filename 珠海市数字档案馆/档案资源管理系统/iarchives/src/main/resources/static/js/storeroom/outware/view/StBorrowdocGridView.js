/**
 * Created by Administrator on 2019/6/12.
 */

Ext.define('Outware.view.StBorrowdocGridView',{
    extend:'Comps.view.BasicGridView',
    xtype:'stBorrowdocGridView',
    itemId:'stBorrowdocGridViewId',
    searchstore:[
        {item:"desci",name:"描述"},
        {item:"borrowman",name:"查档人"}
    ],
    tbar: [{
        itemId:'borrowdocOut',
        xtype: 'button',
        iconCls:'fa fa-reply',
        text: '选择单据'
    }, '-', {
        itemId:'lookDeatail',
        xtype: 'button',
        text: '查看单据详情'
    }, '-', {
        itemId:'back',
        xtype: 'button',
        text: '返回'
    }],
    store: 'StBorrowdocGridStore',
    columns: [
        {text: '查档人', dataIndex: 'borrowman', flex: 2, menuDisabled: true},
        {text: '查档目的', dataIndex: 'borrowmd', flex: 2, menuDisabled: true},
        {text: '查档（接收）单位', dataIndex: 'borroworgan', flex: 2, menuDisabled: true},
        {text: '查档时间', dataIndex: 'borrowdate', flex: 2, menuDisabled: true},
        {text: '申请查档天数', dataIndex: 'borrowts', flex: 2, menuDisabled: true},
        {text: '出库状态', dataIndex: 'outwarestate', flex: 2, menuDisabled: true}
    ]
});
