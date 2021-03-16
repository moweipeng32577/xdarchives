/**
 * Created by xd on 2017/10/21.
 */
Ext.define('StApprove.view.StApproveGridView',{
    extend:'Comps.view.BasicGridView',
    xtype:'stApproveGridView',
    region: 'north',
    height:'40%',
    itemId:'stApproveGridViewID',
    hasSearchBar:false,
    tbar: [{
        itemId:'reject',
        xtype: 'button',
        text: '拒绝'
    }, '-',{
        itemId:'lend',
        xtype: 'button',
        text: '借出'
    },'-',{
        xtype: 'button',
        itemId:'addId',
        iconCls:'fa fa-plus',
        hidden: true,
        text: '添加'
    }, '-', {
        xtype: 'button',
        itemId:'deleteBtnID',
        iconCls:' fa fa-trash-o',
        hidden: true,
        text: '删除'
    }, '-', {
        itemId:'look',
        xtype: 'button',
        text: '查看'
    }],
    store: 'StApproveGridStore',
    columns: [
        {text: '题名', dataIndex: 'title', flex: 2, menuDisabled: true},
        {text: '文件编号', dataIndex: 'filenumber', flex: 2, menuDisabled: true},
        {text: '档号', dataIndex: 'archivecode', flex: 2, menuDisabled: true},
        {text: '全宗号', dataIndex: 'funds', flex: 2, menuDisabled: true},
        {text: '目录号', dataIndex: 'catalog', flex: 2, menuDisabled: true},
        {text: '份数', dataIndex: 'fscount', flex: 2, menuDisabled: true},
        {text: '库存份数', dataIndex: 'kccount', flex: 2, menuDisabled: true},
        {text: '利用权限', dataIndex: 'lyqx', flex: 2, menuDisabled: true}
    ]
});
