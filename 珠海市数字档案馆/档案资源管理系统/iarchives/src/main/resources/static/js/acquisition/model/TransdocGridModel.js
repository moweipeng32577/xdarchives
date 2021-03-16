/**
 * Created by RonJiang on 2018/4/18 0018.
 */
Ext.define('Acquisition.model.TransdocGridModel',{
    extend:'Ext.data.Model',
    fields: [
        {name: 'id', type: 'string',mapping:'docid'},
        {name: 'transdesc', type: 'string'},
        {name: 'transuser', type: 'string'},
        {name: 'transorgan', type: 'string'},
        {name: 'transdate', type: 'string'},
        {name: 'transcount', type: 'string'},
        {name: 'state', type: 'string'},
        {name: 'sendbackreason', type: 'string'}
    ]
});