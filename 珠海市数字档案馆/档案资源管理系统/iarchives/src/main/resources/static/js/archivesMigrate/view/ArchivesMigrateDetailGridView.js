/**
 * Created by Leo on 2020/8/13 0013.
 */
Ext.define('ArchivesMigrate.view.ArchivesMigrateDetailGridView', {
    extend: 'Comps.view.BasicGridView',
    xtype: 'archivesMigrateDetailGridView',
    title: '档案迁移',
    bodyBorder: false,
    head: false,
    searchstore: [
        {item: "title", name: "题名"},
        {item: "archivecode", name: "档号"},
        {item: "filedate", name: "文件日期"},
        {item: "responsible", name: "责任者"},
        {item: "filenumber", name: "文件编号"}
    ],
    tbar: [{
        xtype: 'button',
        itemId:'leadInID',
        iconCls:'fa fa-upload',
        text: '导入'
    }, '-', {
        xtype: 'button',
        itemId:'deleteBtnID',
        iconCls:' fa fa-trash-o',
        text: '删除'
    }, '-', {
        xtype: 'button',
        itemId:'seeBtnID',
        iconCls:'fa fa-eye',
        text: '查看'
    }, '-', {
        xtype: 'button',
        itemId:'back',
        iconCls:'fa fa-undo',
        text: '返回'
    }],
    store: 'ArchivesMigrateDetailGridStore',
    columns: [{
        xtype:'actioncolumn',
        resizable:false,//不可拉伸
        hideable:false,
        header: '原文',
        dataIndex: 'eleid',
        sortable:true,
        width:60,
        align:'center',
        items:['@file']
    },
    {text: '档号', dataIndex: 'archivecode', flex: 1, menuDisabled: true},//档号 实体属性：archivecode
    {text: '题名', dataIndex: 'title', flex: 1, menuDisabled: true},//题名 实体属性：title
    {text: '文件日期', dataIndex: 'filedate', flex: 1, menuDisabled: true},//文件日期 实体属性：filedate
    {text: '责任者', dataIndex: 'responsible', flex: 1, menuDisabled: true},//责任者 实体属性：responsible
    {text: '文件编号', dataIndex: 'filenumber', flex: 1, menuDisabled: true},//文件编号 实体属性：filenumber
    {text: '开放状态', dataIndex: 'flagopen', flex: 1, menuDisabled: true},
    {text: '所属节点', dataIndex: 'nodefullname', flex: 2, menuDisabled: true}]
});