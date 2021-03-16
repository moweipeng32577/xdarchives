Ext.define('CategoryDictionary.model.CategoryDictionaryGridModel',{
    extend:'Ext.data.Model',
    fields: [
        {name: 'id', type: 'string', mapping:'categoryid'},
        {name: 'name', type: 'string'}
    ]
});