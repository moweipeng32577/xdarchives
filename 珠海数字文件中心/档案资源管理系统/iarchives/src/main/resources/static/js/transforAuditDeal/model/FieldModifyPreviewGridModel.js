/**
 * Created by Administrator on 2019/10/26.
 */


Ext.define('TransforAuditDeal.model.FieldModifyPreviewGridModel',{
    extend:'Ext.data.Model',
    fields: [{name: "id", type: "string",mapping:"fieldcode"},
        {name: "fieldname", type: "string"},
        {name: "fieldvalue", type: "string"}]
});
