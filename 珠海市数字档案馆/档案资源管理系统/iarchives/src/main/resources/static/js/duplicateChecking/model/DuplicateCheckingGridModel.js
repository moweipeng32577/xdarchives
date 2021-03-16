/**
 * Created by RonJiang on 2018/01/24
 */
Ext.define('DuplicateChecking.model.DuplicateCheckingGridModel', {
    extend:'Ext.data.Model',
    fields: [
        {name: 'id', type: 'string', mapping: 'entryid'},
        {name: 'electronic', type: 'string'},
        {name: 'archivecode', type: 'string'},
        {name: 'title', type: 'string'},
        {name: 'filedate', type: 'string'},
        {name: 'filenumber', type: 'string'},
        {name: 'responsible', type: 'string'},
        {name: 'retention', type: 'string'}
    ]
});