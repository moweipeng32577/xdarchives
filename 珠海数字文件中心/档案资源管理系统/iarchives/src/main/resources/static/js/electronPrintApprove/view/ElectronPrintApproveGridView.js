/**
 * Created by Administrator on 2019/5/23.
 */

Ext.define('ElectronPrintApprove.view.ElectronPrintApproveGridView',{
    extend:'Comps.view.BasicGridView',
    xtype:'electronPrintApproveGridView',
    region: 'north',
    height:'40%',
    itemId:'electronPrintApproveGridViewID',
    hasSearchBar:false,
    tbar: [{
        xtype: 'button',
        itemId:'agree',
        text: '同意'
    }, '-', {
        xtype: 'button',
        itemId:'refuse',
        text: '拒绝'
    }, '-', {
        itemId:'look',
        xtype: 'button',
        text: '查看'
    },'-',{
        itemId:'setlyqx',
        xtype: 'button',
        text: '设置文件权限'
    }],
    store: 'ElectronPrintApproveGridStore',
    columns: [
        {text: '题名', dataIndex: 'title', flex: 2, menuDisabled: true},
        {text: '文件编号', dataIndex: 'filenumber', flex: 2, menuDisabled: true},
        {text: '档号', dataIndex: 'archivecode', flex: 2, menuDisabled: true},
        {text: '全宗号', dataIndex: 'funds', flex: 2, menuDisabled: true},
        {text: '目录号', dataIndex: 'catalog', flex: 2, menuDisabled: true},
        {text: '利用权限', dataIndex: 'lyqx', flex: 2, menuDisabled: true}
    ]
});
