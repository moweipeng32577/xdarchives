/**
 * Created by RonJiang on 2018/1/22 0022.
 */
Ext.define('Backup.view.BackupView', {
    extend:'Ext.tab.Panel',
    xtype:'backup',
    itemId:'backupViewId',
    tabPosition:'top',
    tabRotation:0,
    activeTab:0,
    items:[{
        title:'设置数据',
        itemId:'backupSettingTab',
        xtype:'backupSetting'
    },{
        title:'业务数据',
        itemId:'backupDataTab',
        xtype:'backupData'
    },{
        title:'数据库备份',
        itemId:'backupDatabaseTab',
        xtype:'backupDatabase'
    }]
});