Ext.define('DigitalInspection.model.DigitalInspectionEntryMediaGridModel',{
    extend:'Ext.data.Model',
    fields: [
        {name: 'id', type: 'string'},
        {name: 'batchcode', type: 'string'},
        {name: 'medianame', type: 'string'},
        {name: 'entryid', type: 'string'},
        {name: 'eleid', type: 'string'},
        {name: 'status', type: 'string'}
    ]
});