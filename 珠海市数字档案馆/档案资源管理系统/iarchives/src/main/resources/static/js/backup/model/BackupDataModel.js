/**
 * Created by RonJiang on 2018/1/23 0023.
 */
Ext.define('Backup.model.BackupDataModel', {
    extend: 'Ext.data.Model',
    xtype:'backupDataModel',
    fields: [{name: "id", type: "string"},
        {name: "text", type: "string"}]
});