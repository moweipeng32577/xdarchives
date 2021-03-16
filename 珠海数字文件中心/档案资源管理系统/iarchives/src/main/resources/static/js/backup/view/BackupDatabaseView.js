/**
 * 数据库备份
 * Created by Rong on 2020.6.9
 */
Ext.define('Backup.view.BackupDatabaseView', {
    extend:'Ext.panel.Panel',
    xtype:'backupDatabase',
    itemId:'backupDatabaseViewId',
    layout:'border',
    items:[{
        region:'north',
        height: 50,
        layout: {
            type: 'hbox',
            align: 'middle'
        },
        items:[{
            width:150,
            xtype: 'button',
            text: '备份',
            itemId: 'backupAll',
            margin:'5 5 5 5'
        },{
            width:170,
            xtype: 'button',
            text: '备份管理',
            itemId: 'historygrid',
            margin:'5 5 5 5'
        },{
            width:170,
            xtype: 'button',
            text: '备份策略',
            itemId: 'dataBackupStrategy',
            margin:'5 5 5 5'
        }]
    }]
});