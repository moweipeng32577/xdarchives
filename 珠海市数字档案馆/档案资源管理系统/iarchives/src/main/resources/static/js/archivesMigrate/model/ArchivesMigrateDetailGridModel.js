/**
 * Created by Leo on 2020/8/13 0013.
 */
Ext.define('ArchivesMigrate.model.ArchivesMigrateDetailGridModel',{
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