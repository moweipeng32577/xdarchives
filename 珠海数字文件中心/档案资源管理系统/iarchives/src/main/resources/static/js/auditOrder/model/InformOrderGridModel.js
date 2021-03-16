/**
 * Created by Administrator on 2020/6/16.
 */


Ext.define('AuditOrder.model.InformOrderGridModel',{
    extend:'Ext.data.Model',
    fields: [
        {name: 'informid', type: 'string'},
        {name: 'title', type: 'string'},
        {name: 'text', type: 'string'}
    ]
});
