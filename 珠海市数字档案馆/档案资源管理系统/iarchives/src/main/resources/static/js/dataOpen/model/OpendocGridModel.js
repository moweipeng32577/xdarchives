/**
 * Created by RonJiang on 2018/4/21 0021.
 */
Ext.define('Dataopen.model.OpendocGridModel',{
    extend:'Ext.data.Model',
    fields: [
        {name: 'id', type: 'string'},
        {name: 'title', type: 'string'},
        {name: 'approvaldate', type: 'string'},
        {name: 'total', type: 'string'},
        {name: 'reason', type: 'string'},
        {name: 'submitter', type: 'string'}
    ]
});