Ext.define('DataEvent.model.DataEventModel',{
    extend:'Ext.data.Model',
    fields: [
        {name: 'id', type: 'string',mapping:'eventid'},
        {name: 'eventname', type: 'string'},
        {name: 'eventnumber', type: 'string'}
    ]
});