/**
 * Created by Administrator on 2019/6/24.
 */

Ext.define('AcceptDirectory.model.AcceptDirectoryModel',{
    extend:'Ext.data.Model',
    fields: [{name: "id", type: "string"},
        {name: "text", type: "string"},
        {name: "leaf", type: "boolean"}]
});