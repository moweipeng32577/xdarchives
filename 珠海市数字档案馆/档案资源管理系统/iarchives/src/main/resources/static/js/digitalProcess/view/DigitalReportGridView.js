/**
 * Created by wangmh on 2019/3/5.
 */
Ext.define('DigitalProcess.view.DigitalReportGridView', {
    extend: 'Comps.view.BasicGridView',
    xtype:'DigitalReportGridView',
    region: 'center',
    itemId:'DigitalReportGridViewID',
    hasCheckColumn:false,
    searchstore:[
        {item: "reportname", name: "报表名称"}
    ],
    tbar: [{
        itemId:'print',
        xtype: 'button',
        text: '打印'
    },'-',{
        itemId:'back',
        xtype: 'button',
        text: '返回'
    }],
    store: 'DigitalReportStore',
    columns: [
        {text: '报表名称', dataIndex: 'reportname', flex: 2, menuDisabled: true}
    ]
});