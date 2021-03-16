/**
 * Created by RonJiang on 2018/1/25 0025.
 */
Ext.define('Management.model.BatchModifyTemplateEnumfieldModel', {
    extend: 'Ext.data.Model',
    xtype: 'batchModifyTemplateEnumfieldModel',
    fields: [{name: "code", type: "string"},
        {name: "value", type: "string"}]
});