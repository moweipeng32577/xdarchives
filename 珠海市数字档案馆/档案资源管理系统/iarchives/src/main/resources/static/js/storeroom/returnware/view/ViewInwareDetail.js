/**
 * Created by Administrator on 2019/5/29.
 */
Ext.define('ReturnWare.view.ViewInwareDetail', {
    extend:'Comps.view.EntryGridView',
    xtype:'ViewInwareDetail',
    hasCloseButton:false,
    searchstore:[
        {item: "archivecode", name: "档号"},
        {item: "title", name: "题名"},
        {item: "filedate", name: "文件日期"},
        {item: "responsible", name: "责任者"},
        {item: "filenumber", name: "文件编号"},
        {item: "flagopen", name: "开放状态"}
    ],
    store: 'InwareDetailStore',
    columns: [
        {
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
        {text: '档号', dataIndex: 'archivecode', flex: 2},//档号 实体属性：archivecode
        {text: '题名', dataIndex: 'title', flex: 3, menuDisabled: true},//题名 实体属性：title
        {text: '文件日期', dataIndex: 'filedate', flex: 1, menuDisabled: true},//文件日期 实体属性：filedate
        {text: '责任者', dataIndex: 'responsible', flex: 1, menuDisabled: true},//责任者 实体属性：responsible
        {text: '文件编号', dataIndex: 'filenumber', flex: 1, menuDisabled: true},//文件编号 实体属性：filenumber
        {text: '开放状态', dataIndex: 'flagopen', flex: 1, menuDisabled: true}
    ],
    tbar: [{
        itemId:'deleteEntry',
        xtype: 'button',
        iconCls:' fa fa-trash-o',
        text: '删除'
    },'-',{
        itemId:'back',
        xtype: 'button',
        iconCls:'fa fa-undo',
        text: '返回'
    }]
});