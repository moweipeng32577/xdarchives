/**
 * Created by Rong on 2017/10/24.
 */
Ext.define('Management.model.ManagementModel',{
    extend:'Ext.data.Model',
    fields: [{name: "id", type: "string"},
        {name: "text", type: "string"},
        {name: "leaf", type: "boolean"}]
});