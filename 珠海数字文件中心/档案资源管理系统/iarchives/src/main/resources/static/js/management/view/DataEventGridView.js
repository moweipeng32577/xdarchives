Ext.define('Management.view.DataEventGridView',{
	extend: 'Comps.view.BasicGridView',
    xtype:'dataEventGridView',
    region: 'center',
    tbar: [{
        xtype: 'button',
        text: '选择',
        iconCls:'fa fa-columns',
        itemId: 'selectEvent'
    },{
        xtype: 'button',
        text: '返回',
        iconCls:'fa fa-undo',
        itemId: 'backEvent'
    }],
    store: 'DataEventStore',
    columns: [
    	{text: '事件编号', dataIndex: 'eventid', flex: 2, menuDisabled: true, hidden: true},
        {text: '事件描述', dataIndex: 'eventname', flex: 2, menuDisabled: true},
        {text: '事件代号', dataIndex: 'eventnumber', flex: 2, menuDisabled: true}
    ],
    hasSearchBar:false
});