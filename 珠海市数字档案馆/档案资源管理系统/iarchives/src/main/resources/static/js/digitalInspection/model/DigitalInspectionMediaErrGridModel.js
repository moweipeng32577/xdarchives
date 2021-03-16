Ext.define('DigitalInspection.model.DigitalInspectionEntryMediaGridModel',{
    extend:'Ext.data.Model',
    fields: [
        {name: 'id', type: 'string'},
        {name: 'batchcode', type: 'string'},
        {name: 'mediaid', type: 'string'},
        {name: 'errtype', type: 'string'},
        {name: 'depict', type: 'string'},
        {name: 'filename', type: 'string'},
        {name: 'status', type: 'string'}
    ]
});