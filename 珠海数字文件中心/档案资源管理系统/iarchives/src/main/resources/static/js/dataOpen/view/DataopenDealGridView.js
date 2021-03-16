/**
 * Created by tanly on 2017/12/2 0002.
 */
Ext.define('Dataopen.view.DataopenDealGridView', {
    extend: 'Comps.view.BasicGridView',
    xtype: 'dataopenDealGridView',
    itemId: 'dataopenDealGridId',
    region: 'center',
    hasSearchBar:false,
    tbar: [{
        itemId:'send',
        xtype: 'button',
        iconCls:'fa fa-share-square',
        text: '开始送审'
    },{
        itemId:'remove',
        xtype:'button',
        iconCls:' fa fa-trash-o',
        text:'移除'
    },{
        itemId:'back',
        xtype: 'button',
        iconCls:'fa fa-undo',
        text: '返回'
    }],
    store: 'DataopenDealGridStore',
    columns: [
    	{text: '数据节点全名', dataIndex: 'nodefullname', flex: 4, menuDisabled: true},
        {text: '题名', dataIndex: 'title', flex: 2, menuDisabled: true},
        {text: '文件编号', dataIndex: 'filenumber', flex: 2, menuDisabled: true},
        {text: '档号', dataIndex: 'archivecode', flex: 2, menuDisabled: true},
        {text: '文件日期', dataIndex: 'filedate', flex: 2, menuDisabled: true},
        {text: '全宗号', dataIndex: 'funds', flex: 2, menuDisabled: true},
        {text: '目录号', dataIndex: 'catalog', flex: 2, menuDisabled: true}
    ]
});