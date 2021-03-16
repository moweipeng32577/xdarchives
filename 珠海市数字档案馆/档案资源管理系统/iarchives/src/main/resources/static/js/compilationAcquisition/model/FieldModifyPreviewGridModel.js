/**
 * Created by RonJiang on 2018/1/26 0026.
 */
Ext.define('CompilationAcquisition.model.FieldModifyPreviewGridModel',{
    extend:'Ext.data.Model',
    fields: [{name: "id", type: "string",mapping:"fieldcode"},
        {name: "fieldname", type: "string"},
        {name: "fieldvalue", type: "string"}]
});