/**
 * Created by yl on 2017/11/3.
 */
Ext.define('ExchangeStorage.view.ExchangeStorageGridView', {
    extend: 'Comps.view.BasicGridView',
    xtype: 'exchangeStorageGridView',
    region: 'center',
    searchstore:[{item: "filename", name: "文件名称"}],
    store: 'ExchangeStorageGridStore',
    columns: [
        {text: '文件名称', dataIndex: 'filename', width:380, menuDisabled: true},
        {text: 'MD5校验值', dataIndex: 'filemd5', width:300, menuDisabled: true},
        {text: '文件大小(KB)', dataIndex: 'filesize', width:200, menuDisabled: true},
        {text: '导入时间', dataIndex: 'filetime', width: 200, menuDisabled: true},
        {text:'四性验证',dataIndex:'validate',width:80}
    ]
});