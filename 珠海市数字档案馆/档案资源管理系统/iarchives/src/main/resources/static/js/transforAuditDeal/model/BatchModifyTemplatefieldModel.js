/**
 * Created by Administrator on 2019/10/26.
 */


Ext.define('TransforAuditDeal.model.BatchModifyTemplatefieldModel', {
    extend: 'Ext.data.Model',
    xtype: 'batchModifyTemplatefieldModel',
    fields: [{name: "fieldcode", type: "string"},
        {name: "fieldname", type: "string"}]
});
