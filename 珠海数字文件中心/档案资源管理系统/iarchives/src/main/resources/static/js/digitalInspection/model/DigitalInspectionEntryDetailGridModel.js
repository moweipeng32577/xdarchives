Ext.define('DigitalInspection.model.DigitalInspectionEntryDetailGridModel',{
    extend:'Ext.data.Model',
    fields: [
        {name: 'id', type: 'string'},
        {name: 'batchcode', type: 'string'},
        {name: 'filecode', type: 'string'},
        {name: 'archivecode', type: 'string'},
        {name: 'captureentryid', type: 'string'},
        {name: 'nodeid', type: 'string'},
        {name: 'pagenum', type: 'string'},
        {name: 'status', type: 'string'}
    ]
});