/**
 * Created by RonJiang on 2018/1/22 0022.
 */
Ext.define('Backup.model.BackupSettingModel', {
    extend: 'Ext.data.Model',
    xtype:'backupSettingModel',
    fields: [{name: "id", type: "string"},
        {name: "text", type: "string"}]
});