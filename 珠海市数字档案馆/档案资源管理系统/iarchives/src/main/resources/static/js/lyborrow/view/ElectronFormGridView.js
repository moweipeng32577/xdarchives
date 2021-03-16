/**
 * Created by yl on 2017/11/3.
 */
Ext.define('Borrow.view.ElectronFormGridView', {
    extend: 'Comps.view.BasicGridView',
    xtype: 'electronFormGridView',
    itemId: 'electronFormGridViewId',
    region: 'south',
    height: '40%',
    store: 'ElectronFormGridStore',
    hasSearchBar: false,
    columns: [
        {text: '题名', dataIndex: 'title', flex: 2, menuDisabled: true},
        {text: '文件编号', dataIndex: 'filenumber', flex: 2, menuDisabled: true},
        {text: '档号', dataIndex: 'archivecode', flex: 2, menuDisabled: true},
        {text: '全宗号', dataIndex: 'funds', flex: 2, menuDisabled: true},
        {text: '目录号', dataIndex: 'catalog', flex: 2, menuDisabled: true}
    ]
});