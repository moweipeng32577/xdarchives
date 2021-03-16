Ext.define('DigitalInspection.view.DigitalInspectionSearchGridView',{
    extend: 'Comps.view.BasicGridView',
    xtype:'DigitalInspectionSearchGridView',
    title: '当前位置：导入条目',
    hasSearchBar:false,//无需基础表格组件中的检索栏
    bookmarkStatus:false,//当前是否切换到个人收藏界面操作
    tbar: [
        {
        itemId:'entryImportId',
        xtype: 'button',
        iconCls:'fa fa-share',
        text: '导入'
    },
    // {
    //     itemId:'entryImportBackId',
    //     xtype: 'button',
    //     iconCls:'fa fa-reply',
    //     text: '返回'
    // }
    ],
    store: 'DigitalInspectionSearchGridStore',
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
        {text: '库存份数', dataIndex: 'kccount', flex: 1, menuDisabled: true},//库存份数
        {text: '开放状态', dataIndex: 'flagopen', flex: 1, menuDisabled: true},
        {text: '所属节点', dataIndex: 'nodefullname', flex: 2, menuDisabled: true}
    ]
});