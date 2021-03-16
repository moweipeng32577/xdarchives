/**
 * Created by Administrator on 2019/6/26.
 */


Ext.define('ManageDirectory.model.FieldModifyPreviewGridModel',{
    extend:'Ext.data.Model',
    fields: [{name: "id", type: "string",mapping:"fieldcode"},
        {name: "fieldname", type: "string"},
        {name: "fieldvalue", type: "string"}]
});
