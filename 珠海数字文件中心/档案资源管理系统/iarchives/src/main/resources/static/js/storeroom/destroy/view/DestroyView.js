/**
 * Created by zengdw on 2018/05/09 0001.
 */

Ext.define('Destroy.view.DestroyView', {
    extend: 'Ext.panel.Panel',
    xtype: 'destroyView',
    layout:'border',
    items:[{
        region:'center',
        xtype:'basicgrid',
        itemId:'destroygrid',
        store:'DestroyStore',
        hasSearchBar:false,
        columns: [
            {text: '存储位置', dataIndex: 'entrystorage', flex: 2, menuDisabled: true},
            {text: '保管期限', dataIndex: 'entryretention', flex: 2, menuDisabled: true},
            {text: '题名', dataIndex: 'title', flex: 2, menuDisabled: true},
            {text: '档号', dataIndex: 'archivecode', flex: 1, menuDisabled: true},
            {text: '文件时间', dataIndex: 'filedate', flex: 2, menuDisabled: true}

        ]
    }]

});