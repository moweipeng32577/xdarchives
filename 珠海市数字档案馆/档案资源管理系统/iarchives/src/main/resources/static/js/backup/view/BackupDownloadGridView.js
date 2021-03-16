/**
 * Created by RonJiang on 2018/1/23 0023.
 */
Ext.define('Backup.view.BackupDownloadGridView', {
    extend:'Comps.view.BasicGridView',
    xtype:'backupDownloadGrid',
    hasPageBar:false,
    hasSearchBar:false,
    store:'BackupDownloadGridStore',
    tbar: [{
        text:'下载',
        iconCls:'fa fa-download',
        itemId:'download'
    },'-',{
        text:'删除',
        iconCls:'fa fa-trash-o',
        itemId:'delete'
    },'-',{
        text:'关闭',
        iconCls:'fa fa-arrow-left',
        itemId:'close'
    }],
    columns: [
        {text: '文件名', dataIndex: 'filename', flex: 6, menuDisabled: true},
        {text: '文件大小(MB)', dataIndex: 'filesize', flex: 2, menuDisabled: true},
        {text: '文件时间', dataIndex: 'filetime', flex: 3, menuDisabled: true}
    ]
});