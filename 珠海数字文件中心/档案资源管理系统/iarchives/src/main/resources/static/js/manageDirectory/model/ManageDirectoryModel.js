/**
 * Created by Administrator on 2019/6/25.
 */

Ext.define('ManageDirectory.model.ManageDirectoryModel',{
    extend:'Ext.data.Model',
    fields: [{name: "id", type: "string"},
        {name: "text", type: "string"},
        {name: "leaf", type: "boolean"}]
});
