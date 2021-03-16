/**
 * Created by Administrator on 2020/10/15.
 */


Ext.define('YearlyCheckAudit.model.YearlyCheckAuditGridModel',{
    extend:'Ext.data.Model',
    fields: [
        {name: 'id', type: 'string'},
        {name: 'text', type: 'string'},
        {name: 'type', type: 'string'}
    ]
});
