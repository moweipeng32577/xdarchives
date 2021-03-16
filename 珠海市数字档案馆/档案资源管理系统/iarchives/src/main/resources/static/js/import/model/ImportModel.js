/**
 * Created by SunK on 2018/8/3 0003.
 */
Ext.define('Import.model.ImportModel',{
    extend:'Ext.data.Model',
    fields: [{name: "id", type: "string"},
        {name: "text", type: "string"},
        {name: "leaf", type: "boolean"}]
});