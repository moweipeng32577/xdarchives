/**
 * Created by yl on 2017/11/3.
 */
Ext.define('Borrow.view.LookAddMxGridView', {
    extend: 'Comps.view.BasicGridView',
    xtype: 'lookAddMxGridView',
    itemId: 'lookAddMxGridViewId',
    region: 'center',
    hasSearchBar: false,
    tbar: [{
        itemId: 'stAddSq',
        xtype: 'button',
        text: '查档申请'
    },{
        itemId:'remove',
        xtype:'button',
        text:'移除'
    }, {
        itemId: 'close',
        xtype: 'button',
        text: '返回'
    }],
    store: 'LookAddMxGridStore',
    columns: [
        {text: '题名', dataIndex: 'title', flex: 2, menuDisabled: true},
        {text: '文件编号', dataIndex: 'filenumber', flex: 2, menuDisabled: true},
        {text: '档号', dataIndex: 'archivecode', flex: 2, menuDisabled: true},
        {text: '全宗号', dataIndex: 'funds', flex: 2, menuDisabled: true},
        {text: '目录号', dataIndex: 'catalog', flex: 2, menuDisabled: true}
    ]
});