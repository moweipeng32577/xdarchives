/**
 * Created by yl on 2017/11/3.
 */
Ext.define('SimpleSearch.view.ElectronFormGridView', {
    extend: 'Comps.view.BasicGridView',
    xtype: 'electronFormGridView',
    itemId: 'electronFormGridViewId',
    region: 'south',
    height: '45%',
    store: 'ElectronFormGridStore',
    hasSearchBar: false,
    tbar:[
        {
            itemId:'setType',
            xtype:'button',
            text:'设置查档类型'
        }
    ],
    columns: [
        {
            xtype:'actioncolumn',
            resizable:false,//不可拉伸
            hideable:false,
            header: '原文',
            dataIndex: 'eleid',
            sortable:true,
            width:60,
            align:'center',
            items:['@file']
        },
        {text: '题名', dataIndex: 'title', flex: 2, menuDisabled: true},
        {text: '文件编号', dataIndex: 'filenumber', flex: 2, menuDisabled: true},
        {text: '档号', dataIndex: 'archivecode', flex: 2, menuDisabled: true},
        {text: '全宗号', dataIndex: 'funds', flex: 2, menuDisabled: true},
        {text: '目录号', dataIndex: 'catalog', flex: 2, menuDisabled: true},
        {text: '库存份数', dataIndex: 'kccount', flex: 2, menuDisabled: true},
        {text: '查档类型', dataIndex: 'flagopen', flex: 2, menuDisabled: true}
    ]
});