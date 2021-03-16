/**
 * Created by Administrator on 2020/6/13.
 */


Ext.define('AuditOrder.model.AuditOrderGridModel',{
    extend:'Ext.data.Model',
    fields: [
        {name: 'id', type: 'string'},
        {name: 'text', type: 'string'},
        {name: 'type', type: 'string'}
    ]
});
