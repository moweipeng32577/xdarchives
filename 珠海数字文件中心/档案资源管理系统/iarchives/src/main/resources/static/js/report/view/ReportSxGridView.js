/**
 * Created by RonJiang on 2018/2/27 0027.
 */
Ext.define('Report.view.ReportSxGridView', {
    extend: 'Comps.view.BasicGridView',
    xtype:'reportSxGridView',
    region: 'center',
    itemId:'reportSxGridViewID',
    searchstore:[
        {item: "reportname", name: "报表名称"},
        {item: "modul", name: "模板名称"},
        {item: "reporttype", name: "报表访问类型"},
        {item: "printfieldnamelist", name: "打印字段"},
        {item: "orderfieldname", name: "排序字段"}
    ],
    tbar: [{
        itemId:'save',
        xtype: 'button',
        iconCls:'fa fa-plus-circle',
        text: '增加'
    }, '-', {
        itemId:'modify',
        xtype: 'button',
        iconCls:'fa fa-pencil-square-o',
        text: '修改'
    }, '-', {
        itemId:'del',
        xtype: 'button',
        iconCls:'fa fa-trash-o',
        text: '删除'
    }, '-', {
        itemId:'look',
        xtype: 'button',
        text: '查看',
        iconCls:'fa fa-eye'
    },'-', {
        itemId:'reportStyleFileManage',
        xtype: 'button',
        iconCls:'fa fa-bars',
        text: '报表样式管理'
    }],
    store: 'ReportGridStore',
    columns: [
        {text: '报表名称', dataIndex: 'reportname', flex: 2, menuDisabled: true},
        {text: '模板名称', dataIndex: 'modul', flex: 2, menuDisabled: true},
        {text: '报表访问类型', dataIndex: 'reporttype', flex: 2, menuDisabled: true},
        //{text: '打印字段', dataIndex: 'printfieldnamelist', flex: 2, menuDisabled: true},
        //{text: '排序字段', dataIndex: 'orderfieldname', flex: 2, menuDisabled: true}
    ]
});