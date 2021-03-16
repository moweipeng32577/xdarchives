/**
 * Created by tanly on 2017/12/5.
 */
Ext.define('OpenApprove.model.OpenApproveGridModel',{
    extend:'Ext.data.Model',
    fields: [
        {name: 'id', type: 'string', mapping: 'entryid'},
        {name: 'title', type: 'string'},
        {name: 'filenumber', type: 'string'},
        {name: 'archivecode', type: 'string'},
        {name: 'funds', type: 'string'},
        {name: 'catafog', type: 'string'},
        {name: 'lyqx', type: 'string'}
    ]
});