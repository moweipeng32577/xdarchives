/**
 * Created by RonJiang on 2018/03/08
 */
Ext.define('Management.view.ManagementReportGridView', {
    extend: 'Comps.view.BasicGridView',
    xtype:'managementReportGridView',
    region: 'center',
    itemId:'reportGridViewID',
    hasCheckColumn:false,
    searchstore:[
        {item: "reportname", name: "报表名称"},
        {item: "modul", name: "模板名称"},
        {item: "reporttype", name: "报表访问类型"},
        {item: "printfieldnamelist", name: "打印字段"},
        {item: "orderfieldname", name: "排序字段"}
    ],
    tbar: [{
        itemId:'print',
        xtype: 'button',
        text: '打印'
    }, '-', {
        itemId:'showAllReport',
        xtype: 'button',
        text: '显示所有报表'
    }, '-', {
        itemId:'back',
        xtype: 'button',
        text: '返回'
    }],
    store: 'ReportGridStore',
    columns: [
        {text: '报表名称', dataIndex: 'reportname', flex: 2, menuDisabled: true},
        {text: '模板名称', dataIndex: 'modul', flex: 2, menuDisabled: true},
        {text: '报表访问类型', dataIndex: 'reporttype', flex: 2, menuDisabled: true},
        {text: '打印字段', dataIndex: 'printfieldnamelist', flex: 2, menuDisabled: true},
        {text: '排序字段', dataIndex: 'orderfieldname', flex: 2, menuDisabled: true}
    ]
});