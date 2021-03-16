Ext.define('DataEvent.view.DataEventDetailGridView', {
	dataUrl: '/dataEvent/lookEventEntry',
    extend:'Comps.view.EntryGridView',
    xtype: 'dataEventDetailGridView',
    title: '数据关联',
    hasSearchBar:false,
    tbar: [{
        xtype: 'button',
        itemId:'leadInID',
        iconCls:'fa fa-upload',
        text: '导入'
    }, '-', {
        xtype: 'button',
        itemId:'deleteBtnID',
        iconCls:' fa fa-trash-o',
        text: '删除'
    }, '-', {
        xtype: 'button',
        itemId:'seeBtnID',
        iconCls:'fa fa-eye',
        text: '查看'
    }, '-', {
        xtype: 'button',
        itemId:'back',
        iconCls:'fa fa-undo',
        text: '返回'
    }]
});