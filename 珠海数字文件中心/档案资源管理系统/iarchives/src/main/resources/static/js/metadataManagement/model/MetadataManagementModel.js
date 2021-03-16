/**
 * Created by SunK on 2018/7/31 0031.
 */
Ext.define('MetadataManagement.model.MetadataManagementModel',{
    extend:'Ext.data.Model',
    fields: [{name: "id", type: "string"},
        {name: "text", type: "string"},
        {name: "leaf", type: "boolean"}]
});