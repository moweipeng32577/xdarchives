/**
 * Created by Administrator on 2019/6/26.
 */


Ext.define('ManageDirectory.model.BatchModifyTemplateEnumfieldModel', {
    extend: 'Ext.data.Model',
    xtype: 'batchModifyTemplateEnumfieldModel',
    fields: [{name: "code", type: "string"},
        {name: "value", type: "string"}]
});
