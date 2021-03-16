/**
 * Created by Administrator on 2019/5/17.
 */


Ext.define('SimpleSearch.view.ApplyPrintGridView', {
    extend: 'Comps.view.BasicGridView',
    xtype: 'applyPrintGridView',
    itemId: 'applyPrintGridViewId',
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
    ],
    tbar:[
       {
            itemId:'editPrint',
            xtype:'button',
            text:'修改打印范围'
        }
    ]
});
