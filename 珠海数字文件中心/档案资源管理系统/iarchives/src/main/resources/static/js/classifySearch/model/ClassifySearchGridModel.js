/**
    * Created by RonJiang on 2017/10/24 0024.
    */
Ext.define('ClassifySearch.model.ClassifySearchGridModel',{
    extend:'Ext.data.Model',
    fields: [
        {name: 'id', type: 'string', mapping: 'entryid'},
        {name: 'electronic', type: 'string'},
        {name: 'archivecode', type: 'string'},
        {name: 'title', type: 'string'},
        {name: 'filedate', type: 'string'},
        {name: 'responsible', type: 'string'},
        {name: 'filenumber', type: 'string'},
        {name: 'archivetype', type: 'string'},
        {name: 'classifyname', type: 'string'}
    ]
});