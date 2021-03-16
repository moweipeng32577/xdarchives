/**
 * Created by yl on 2017/11/2.
 */
Ext.define('ExchangeTransfer.view.ExchangeTransferGridView', {
    extend: 'Comps.view.BasicGridView',
    xtype: 'exchangeTransferGridView',
    region: 'center',
    searchstore:[{item: "filename", name: "文件名称"}],
    tbar: [{
        xtype: 'button',
        itemId:'transferBtnID',
        text: '数据移交',
        iconCls:'fa fa-random'
    },{
        xtype: 'button',
        itemId:'delete',
        text: '删除',
        iconCls:'fa fa-trash-o'
    }],
    store: 'ExchangeTransferGridStore',
    columns: [
        {text: '文件名称', dataIndex: 'filename', width:380, menuDisabled: true},
        {text: 'MD5校验值', dataIndex: 'filemd5', width:300, menuDisabled: true},
        {text: '文件大小(KB)', dataIndex: 'filesize', width:200, menuDisabled: true},
        {text: '导入时间', dataIndex: 'filetime', width: 200, menuDisabled: true}
    ]
});
