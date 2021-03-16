/**
 * Created by RonJiang on 2018/1/25 0025.
 */
Ext.define('CompilationAcquisition.model.BatchModifyTemplatefieldModel', {
    extend: 'Ext.data.Model',
    xtype: 'batchModifyTemplatefieldModel',
    fields: [{name: "fieldcode", type: "string"},
        {name: "fieldname", type: "string"}]
});