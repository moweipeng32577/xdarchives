/**
 * Created by SunK on 2020/6/23 0023.
 */
Ext.define('Acquisition.view.ServiceMetadataGridView',{
    extend: 'Comps.view.BasicGridView',
    xtype:'serviceMetadataGridView',
    // hasSearchBar:false,
    hasCloseButton:false,
    // hasPageBar:false,
    hasCheckColumn:false,
    hasSearchBar:false,
    searchstore: [
        {item: "operation", name: "业务行为"},
        {item: "mstatus", name: "业务状态"},
        {item: "servicetime", name: "业务时间"},
        {item: "operationmsg", name: "业务描述"}
    ],
    store: 'ServiceMetadataGridStore',
    columns: [
        {text: '业务行为', dataIndex: 'operation', flex: 1, menuDisabled: true},
        {text: '业务状态', dataIndex: 'mstatus', flex: 2, menuDisabled: true},
        {text: '业务时间', dataIndex: 'servicetime', flex: 3, menuDisabled: true},
        {text: '业务描述', dataIndex: 'operationmsg', flex: 1, menuDisabled: true}
    ]
});