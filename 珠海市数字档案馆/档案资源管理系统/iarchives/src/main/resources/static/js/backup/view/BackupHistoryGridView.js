/**
 * Created by tanly on 2018/1/25 0025.
 */
Ext.define('Backup.view.BackupHistoryGridView', {
    extend: 'Ext.window.Window',
    xtype: 'BackupHistoryGridView',
    title: '历史备份',
    width: 780,
    height: 500,
    modal: true,
    closeToolText: '关闭',
    layout: 'fit',
    items: [{
        xtype:'backupDownloadGrid',
        itemId:'backupDownloadGridItem'
    }]
});