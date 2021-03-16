Ext.define('CompilationAcquisition.model.ManagementClassificationModel',{
    extend:'Ext.data.Model',
    fields: [
        {name: 'id', type: 'string', mapping:'entryid'},
        {name: 'archivecode', type: 'string'},
        {name: 'title', type: 'string'},
        {name: 'filingyear', type: 'string'},
        {name: 'entryretention', type: 'string'},
        {name: 'organ', type: 'string'}
    ]
});