/**
 * Created by Administrator on 2019/6/26.
 */


Ext.define('ManageDirectory.model.BatchModifyTemplatefieldModel', {
    extend: 'Ext.data.Model',
    xtype: 'batchModifyTemplatefieldModel',
    fields: [{name: "fieldcode", type: "string"},
        {name: "fieldname", type: "string"}]
});
