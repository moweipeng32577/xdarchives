/**
 * Created by xd on 2017/10/21.
 */
Ext.define('WhthinManage.view.DzJyGridView',{
    extend:'Comps.view.BasicGridView',
    xtype:'dzJyGridView',
    title: '',
    region: 'center',
    itemId:'dzJyGridViewID',
    hasSearchBar:false,
    tbar: [{
            itemId:'dzDealDetailsId',
            xtype: 'button',
            iconCls:'fa fa-newspaper-o',
            text: '办理详情'
    }, '-', {
        itemId:'lookBorrowMsgId',
        xtype: 'button',
        iconCls:'fa fa-newspaper-o',
        text: '查看单据信息'
    }, '-',{
        itemId:'showFileId',
        xtype: 'button',
        iconCls:'fa fa-newspaper-o',
        text: '出具查无此档证明'
    }, '-', {
        itemId:'print',
        xtype: 'button',
        iconCls:'fa fa-print',
        text: '打印单据清册'
    }, '-', {
        itemId:'appraise',
        xtype: 'button',
        iconCls:'fa fa-check-circle-o',
        text: '使用评分'
    }],
    store: 'DzJyGridStore',
    columns: [
        {text: '描述', dataIndex: 'desci', flex: 4, menuDisabled: true},
        {text: '查档人', dataIndex: 'borrowman', flex: 2, menuDisabled: true},
        {text: '查档目的', dataIndex: 'borrowmd', flex: 2, menuDisabled: true},
        {text: '查档单位', dataIndex: 'borroworgan', flex: 2, menuDisabled: true},
        {text: '查档时间', dataIndex: 'borrowdate', flex: 2, menuDisabled: true},
        {text: '申请查档天数', dataIndex: 'borrowts', flex: 2, menuDisabled: true}
    ]
});
