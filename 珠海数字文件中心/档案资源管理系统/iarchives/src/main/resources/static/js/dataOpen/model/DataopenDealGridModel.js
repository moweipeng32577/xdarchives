/**
 * Created by tanly on 2018/1/6 0006.
 */
Ext.define('Dataopen.model.DataopenDealGridModel',{
    extend:'Ext.data.Model',
    fields: [
        {name: 'id', type: 'string', mapping: 'entryid'},
        {name: 'title', type: 'string'},
        {name: 'filenumber', type: 'string'},
        {name: 'archivecode', type: 'string'},
        {name: 'funds', type: 'string'},
        {name: 'catalog', type: 'string'},
        {name: 'filedate', type: 'string'}
    ]
});