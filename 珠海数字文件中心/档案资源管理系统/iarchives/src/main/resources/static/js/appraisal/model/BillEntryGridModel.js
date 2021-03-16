/**
 * Created by RonJiang on 2018/4/21 0021.
 */
Ext.define('Appraisal.model.BillEntryGridModel',{
    extend:'Ext.data.Model',
    fields: [
        {name: 'id', type: 'string',mapping:'ugid'},
        {name: 'title', type: 'string'},
        {name: 'archivecode', type: 'string'},
        {name: 'entryretention', type: 'string'},
        {name: 'filedate', type: 'string'}
    ]
});