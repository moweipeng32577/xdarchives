/**
 * Created by tanly on 2018/1/26 0026.
 */
Ext.define('Restore.model.RestoreGridModel',{
    extend:'Ext.data.Model',
    fields: [{name: "id", type: "string",mapping:"filename"},
        {name: "filesize", type: "string"},
        {name: "filetime", type: "string"}]
});