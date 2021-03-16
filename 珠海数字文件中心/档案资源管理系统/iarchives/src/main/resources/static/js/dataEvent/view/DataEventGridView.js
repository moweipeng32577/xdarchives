Ext.define('DataEvent.view.DataEventGridView',{
	extend: 'Comps.view.BasicGridView',
    xtype:'dataEventGridView',
    region: 'center',
    tbar: [{
        xtype: 'button',
        text: '增加',
        iconCls:'fa fa-plus-circle',
        itemId: 'addEvent'
    },{
        xtype: 'button',
        text: '修改',
        iconCls:'fa fa-pencil-square-o',
        itemId: 'updateEvent'
    },{
        xtype: 'button',
        text: '删除',
        iconCls:'fa fa-trash-o',
        itemId: 'deleteEvent'
    },{
        xtype: 'button',
        text: '查看',
        iconCls:'fa fa-eye',
        itemId: 'lookEvent'
    }],
    store: 'DataEventStore',
    columns: [
    	{text: '事件编号', dataIndex: 'eventid', flex: 2, menuDisabled: true, hidden: true},
        {text: '事件描述', dataIndex: 'eventname', flex: 2, menuDisabled: true},
        {text: '事件代号', dataIndex: 'eventnumber', flex: 2, menuDisabled: true}
    ],
    hasSearchBar:false
});