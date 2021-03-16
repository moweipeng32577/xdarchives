Ext.define('DigitalInspection.model.DigitalInspectionWclGridModel',{
    extend:'Ext.data.Model',
    fields: [
        {name: 'id', type: 'string'},
        {name: 'batchcode', type: 'string'},
        {name: 'batchname', type: 'string'},
        {name: 'archivetype', type: 'string'},
        {name: 'copies', type: 'string'},
        {name: 'pagenum', type: 'string'},
        {name: 'inspector', type: 'string'},
        {name: 'checkcount', type: 'string'},
        {name: 'status', type: 'string'},
        {name: 'checker', type: 'string'}
    ]
});