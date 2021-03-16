Ext.define('DigitalProcess.view.DealDetailsGridView', {
    extend: 'Comps.view.BasicGridView',
    xtype:'DealDetailsGridView',
    hasSearchBar:false,
    hasCloseButton:false,
    hasPageBar:false,
    hasCheckColumn:false,
    store: 'DealDetailsGridStore',
    columns: [
        {text: '批次', dataIndex: 'batchcode', flex: 2, menuDisabled: true},
        {text: '环节', dataIndex: 'nodename', flex: 2, menuDisabled: true},
        {text: '办理人', dataIndex: 'operator', flex: 1, menuDisabled: true},
        {text: '办理时间', dataIndex: 'operatetime', flex: 2.5, menuDisabled: true}
    ]
});
