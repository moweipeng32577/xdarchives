/**
 * Created by RonJiang on 2018/1/23 0023.
 */
Ext.define('Backup.model.BackupDownloadGridModel',{
    extend:'Ext.data.Model',
    fields: [{name: "id", type: "string",mapping:"filename"},
        {name: "filesize", type: "string"},
        {name: "filetime", type: "string"}]
});