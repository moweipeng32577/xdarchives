/**
 * Created by Administrator on 2019/2/19.
 */

Ext.define('ElectronApprove.view.SimpleSearchGridView',{
    extend: 'Comps.view.BasicGridView',
    xtype:'simpleSearchGridView',
    title: '当前位置：导入',
    itemId:'simpleSearchGridViewId',
    hasSearchBar:false,//无需基础表格组件中的检索栏
    allowDrag:false,
    tbar: [{
        itemId:'searchleadinId',
        xtype: 'button',
        iconCls:'fa fa-upload',
        text: '导入'
    }, '-',{
        itemId:'simpleSearchShowId',
        xtype: 'button',
        iconCls:'fa fa-eye',
        text: '查看'
    }, '-',{
        xtype: 'button',
        itemId:'simpleSearchBackId',
        iconCls:'fa fa-undo',
        text: '返回'
    }],
    store: 'SimpleSearchGridStore',
    columns: [
        {
            xtype:'actioncolumn',
            resizable:false,//不可拉伸
            hideable:false,
            header: '原文',
            dataIndex: 'eleid',
            width:60,
            sortable:true,
            align:'center',
            items:['@file']
        },
        {text: '档号', dataIndex: 'archivecode', flex: 2},//档号 实体属性：archivecode
        {text: '题名', dataIndex: 'title', flex: 3, menuDisabled: true},//题名 实体属性：title
        {text: '文件日期', dataIndex: 'filedate', flex: 1, menuDisabled: true},//文件日期 实体属性：filedate
        {text: '责任者', dataIndex: 'responsible', flex: 1, menuDisabled: true},//责任者 实体属性：responsible
        {text: '文件编号', dataIndex: 'filenumber', flex: 1, menuDisabled: true},//文件编号 实体属性：filenumber
        {text: '全宗号', dataIndex: 'funds', flex: 1, menuDisabled: true},//全宗号  实体属性:funds
        {text: '目录号', dataIndex: 'catalog', flex: 1, menuDisabled: true},//目录号  实体属性:catalog
        {text: '案卷号', dataIndex: 'filecode', flex: 1, menuDisabled: true},//案卷号  实体属性:filecode
        {text: '件号', dataIndex: 'recordcode', flex: 1, menuDisabled: true},//件号  实体属性:recordcode
        {text: '归档年度', dataIndex: 'filingyear', flex: 1, menuDisabled: true},//归档年度  实体属性:filingyear
        {text: '开放状态', dataIndex: 'flagopen', flex: 1, menuDisabled: true},
        {text: '所属节点', dataIndex: 'nodefullname', flex: 2, menuDisabled: true}
    ]
});
