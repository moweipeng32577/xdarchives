/**
 * Created by RonJiang on 2018/1/22 0022.
 */
Ext.define('Backup.store.BackupSettingStore',{
    extend:'Ext.data.TreeStore',
    model:'Backup.model.BackupSettingModel',
    autoLoad:true,
    proxy: {
        type: 'ajax',
        url: '/backupRestore/getSettingBackups',
        reader: {
            type: 'json',
            expanded: true
        }
    },
    root: {
        text: '系统设置',
        expanded: true,
        checked: true
    }
});