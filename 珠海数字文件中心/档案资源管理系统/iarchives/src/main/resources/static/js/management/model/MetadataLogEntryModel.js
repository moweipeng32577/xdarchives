Ext.define('Management.model.MetadataLogEntryModel',{
    extend:'Ext.data.Model',
    fields: [
        {name: 'id', type: 'string'},
        {name: 'operateuser', type: 'string'},
        {name: 'operateusername', type: 'string'},
        {name: 'operatetime', type: 'string'},
        {name: 'ip', type: 'string'},
        {name: 'type', type: 'string'}
    ]
});