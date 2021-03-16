/**
 * Created by Leo on 2020/7/14 0014.
 */
var reportStore = Ext.create("Ext.data.Store", {
    fields: ["text", "value"],
    data: [
        { text: "查档台账统计表", value: "查档台账统计表"},
        { text: "查档类型统计表", value: "查档类型统计表"},
    ]
});

Ext.define('ConsultStandingBook.view.StatisticsPrintView', {
    extend: 'Ext.window.Window',
    xtype: 'statisticsPrintView',
    width: 600,
    height:240,
    minWidth: 600,
    closeToolText:'关闭',
    autoShow: true,
    modal: true,
    resizable: false,//是否可以改变窗口大小
    layout: 'column',
    bodyPadding: 15,
    items: [{
            style: 'width: 85%',
            xtype: 'combo',
            store: reportStore,
            name: 'reportType',
            itemId: 'reportTypeId',
            fieldLabel: '报表类型',
            labelStyle: "text-align:right;padding-top: 6px;",
            displayField: 'value',
            valueField: 'value',
            margin: '12 20 0 20',
            editable: false,
            allowBlank: false,
            listeners: {
                afterrender: function (combo) {
                    var store = combo.getStore();
                    if (store.getCount() > 0) {
                        combo.select(store.getAt(0));
                    }
                },
                select: function (combo) {
                    var selectcombo = combo.lastValue;
                }
            }

    },{
        style: 'width: 85%',
            fieldLabel: '开始日期',
            emptyText: '请选择开始日期',
            xtype: 'datefield',
            name: 'startdate',
            itemId: 'startdateid',
            format: 'Y-m-d',
            labelStyle: "text-align:right;padding-top: 6px;",
            maxValue: new Date(),
            margin: '12 0 0 20',
            listeners: {
                //展开开始日期窗口，关闭结束日期窗口
                expand: function (field) {
                    var endday = this.findParentByType('statisticsPrintView').down('[itemId = enddateid]');
                    endday.collapse();
                },
                select: function (datefield, date) {
                    var endday = this.findParentByType('statisticsPrintView').down('[itemId = enddateid]');
                    // endday.setMinValue(date);
                    Ext.defer(function () {
                        endday.expand();
                    }, 10);
                }
            }

    },{
        style: 'width: 85%',
            emptyText: '请选择结束日期',
            fieldLabel: '结束日期',
            xtype: 'datefield',
            name: 'enddate',
            itemId: 'enddateid',
            format: 'Y-m-d',
            labelStyle: "text-align:right;padding-top: 6px;",
            margin: '12 20 0 20',

    }],
    buttons: [{
        itemId: 'printID',
        text: '打印'
    }, {
        itemId: 'closeBtnID',
        text: '关闭'
    }]
});