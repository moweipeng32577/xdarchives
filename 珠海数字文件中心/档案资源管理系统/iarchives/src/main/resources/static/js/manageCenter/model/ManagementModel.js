/**
 * Created by Administrator on 2020/7/29.
 */


Ext.define('ManageCenter.model.ManagementModel',{
    extend:'Ext.data.Model',
    fields: [{name: "id", type: "string"},
        {name: "text", type: "string"},
        {name: "leaf", type: "boolean"}]
});