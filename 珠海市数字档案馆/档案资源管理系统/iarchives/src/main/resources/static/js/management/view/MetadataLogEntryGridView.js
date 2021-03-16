Ext.define('Management.view.MetadataLogEntryGridView',{
    extend: 'Comps.view.BasicGridView',
    xtype:'MetadataLogEntryGridView',
    // hasSearchBar:false,
    hasCloseButton:false,
    // hasPageBar:false,
    hasCheckColumn:false,
    searchstore: [
        {item: "operateuser", name: "操作人"},
        {item: "operatetime", name: "操作时间"},
        {item: "depict", name: "操作描述"},
        {item: "ip", name: "IP地址"},
        {item: "type", name: "类型"}
    ],
    store: 'MetadataLogEntryStore',
    columns: [
        {text: '操作人', dataIndex: 'operateusername', flex: 1, menuDisabled: true},
        {text: '操作时间', dataIndex: 'operatetime', flex: 2, menuDisabled: true},
        {text: '操作描述', dataIndex: 'depict', flex: 3, menuDisabled: true},
        {text: 'IP地址', dataIndex: 'ip', flex: 1, menuDisabled: true},
        {text: '类型', dataIndex: 'type', flex: 1, menuDisabled: true}
    ]
});