/**
 * Created by Administrator on 2019/9/20.
 */


Ext.define('DigitalProcess.model.DigitalProcessAuditAuditGridModel',{
    extend:'Ext.data.Model',
    fields: [
        {name: 'id', type: 'string'},
        {name: 'batchcode', type: 'string'},
        {name: 'archivecode', type: 'string'}
    ]
});
