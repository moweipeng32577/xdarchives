/**
 * Created by RonJiang on 2018/04/23
 */
Ext.define('Recyclebin.view.RecyclebinGridView', {
    extend:'Comps.view.BasicGridView',
    xtype:'recyclebingrid',
    itemId:'recyclebinGridID',
    store:'RecyclebinGridStore',
    searchstore:[
        {item: 'filename', name: '文件名称'},
        {item: 'filetype', name: '文件类型'},
        {item: 'filepath', name: '文件路径'},
        {item: 'filesize', name: '文件大小'},
        {item: 'deletetime', name: '删除时间'},
        {item: 'originaltable', name: '源数据表'}
    ],
    tbar:[{
        itemId:'restore',
        xtype: 'button',
        iconCls:'fa fa-retweet',
        text: '还原'
    }, '-', {
        itemId:'del',
        xtype: 'button',
        iconCls:'fa fa-trash-o',
        text: '彻底删除'
    }, '-', {
        itemId:'look',
        xtype: 'button',
        iconCls:'fa fa-eye',
        text: '查看'
    }, '-', {
        itemId:'download',
        xtype: 'button',
        iconCls:'fa fa-download',
        text: '下载电子文件'
    }],
    columns: [
        {text: '文件名称', dataIndex: 'filename', flex: 2, menuDisabled: true},
        {text: '文件类型', dataIndex: 'filetype', flex: 2, menuDisabled: true},
        {text: '文件路径', dataIndex: 'filepath', flex: 2, menuDisabled: true},
        {text: '文件大小', dataIndex: 'filesize', flex: 2, menuDisabled: true},
        {text: '删除时间', dataIndex: 'deletetime', flex: 2, menuDisabled: true},
        {text: '源数据表', dataIndex: 'originaltable', flex: 2, menuDisabled: true}
    ]
});