
Ext.define('ReservoirArea.view.ReservoirAreaView', {
    extend: 'Ext.panel.Panel',
    xtype: 'reservoirAreaView',
    layout: 'border',
    items: [{
        region: 'north',
        flex: 0.8,
        itemId: 'storeroomId',
        xtype: 'panel',
        layout: 'hbox',
        margin: '10 5 10 10'
    }, {
        region: 'west',
        flex: 2.5,
        xtype: 'basicgrid',
        itemId: 'zonegrid',
        store: 'RoomZoneStore',
        hasRownumber: false,
        hasSearchBar: false,
        hasCheckColumn: false,
        margin: '10 5 10 10',
        columns: [
            {text: '库房', dataIndex: 'roomdisplay', flex: 1, menuDisabled: true},
            {text: '密集架区', dataIndex: 'zonedisplay', flex: 1, menuDisabled: true},
            {text: '容量', dataIndex: 'capacity', flex: 1, menuDisabled: true},
            {text: '使用量', dataIndex: 'usecapacity', flex: 1, menuDisabled: true},
            {text: '使用率(%)', dataIndex: 'usage', flex: 1, menuDisabled: true},
            {text: 'id号', dataIndex: 'zoneid', flex: 1, hidden: true}
        ]
    }, {
        region: 'center',
        flex: 2.5,
        xtype: 'basicgrid',
        itemId: 'colgrid',
        store: 'RoomColumnStore',
        hasRownumber: false,
        hasSearchBar: false,
        hasCheckColumn: false,
        margin: '10 5 10 10',
        tbar: [{text: '打开图形界面', itemId: 'openTBtn'}],
        columns: [
            {text: '列名', dataIndex: 'coldisplay', flex: 1, menuDisabled: true},
            {text: '容量', dataIndex: 'capacity', flex: 1, menuDisabled: true},
            {text: '使用量', dataIndex: 'usecapacity', flex: 1, menuDisabled: true},
            {text: '使用率(%)', dataIndex: 'usage', flex: 1, menuDisabled: true}
        ]
    }]

});