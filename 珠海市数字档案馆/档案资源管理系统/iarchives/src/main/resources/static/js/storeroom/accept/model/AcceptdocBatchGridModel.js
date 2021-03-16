/**
 * Created by Administrator on 2019/6/18.
 */
Ext.define('Accept.model.AcceptdocBatchGridModel',{
    extend:'Ext.data.Model',
    fields: [
        {name: 'batchid', type: 'string'},
        {name: 'acceptdocid', type: 'string'},
        {name: 'batchcode', type: 'string'},
        {name: 'disinfector', type: 'string'},
        {name: 'disinfectiontime', type: 'string'},
        {name: 'state', type: 'string'},
        {name: 'batchremark', type: 'string'}
    ]
});
