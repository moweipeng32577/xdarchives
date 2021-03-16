/**
 * Created by wangmh on 2019/4/30.
 */
var reportStore = Ext.create("Ext.data.Store", {
    fields: ["text", "value"],
    data: [
        { text: "总数统计表", value: "总数统计表"},
        { text: "维护统计表", value: "维护统计表"},
        { text: "公车统计表", value: "公车统计表"},
        { text: "场地统计表", value: "场地统计表"},
        { text: "公告统计表", value: "公告统计表"},
        { text: "设备统计表", value: "设备统计表"},
        { text: "党风建设统计表", value: "党风建设统计表"},
    ]
});

Ext.define('DataStatistics.view.ReportSearchView',{
    extend: 'Ext.form.Panel',
    xtype: 'ReportSearchView',
    layout:'border',
    items:[
        {
            xtype:'form',
            itemId:'searchId',
            layout:'column',
            region: 'north',
            title:'统计条件栏',
            collapsible:true,
            height: '32%',
            collapsible: true,   // make collapsible
            split: true,         // enable resizing
            items: [
                {
                    columnWidth: .49,
                    style: 'width:100%',
                    xtype: 'combo',
                    store: reportStore,
                    name: 'reportType',
                    itemId: 'reportTypeId',
                    fieldLabel: '报表类型',
                    labelStyle : "text-align:right;padding-top: 6px;",
                    displayField: 'value',
                    valueField: 'value',
                    margin:'10 20 0 20',
                    editable: false,
                    allowBlank:false,
                    listeners: {
                        afterrender: function (combo) {
                            var store = combo.getStore();
                            if (store.getCount() > 0) {
                                combo.select(store.getAt(0));
                            }
                        },
                        select:function (combo) {
                            var  selectcombo =combo.lastValue;
                            if(selectcombo == '总数统计表'){
                                combo.findParentByType('ReportSearchView').down('[itemId=startdateid]').hide();
                                combo.findParentByType('ReportSearchView').down('[itemId=enddateid]').hide();
                            }else{
                                combo.findParentByType('ReportSearchView').down('[itemId=startdateid]').show();
                                combo.findParentByType('ReportSearchView').down('[itemId=enddateid]').show();
                            }
                        }
                    }
                },
                {
                    columnWidth: .49,
                    fieldLabel: '开始日期',
                    emptyText: '请选择开始日期',
                    xtype: 'datefield',
                    name: 'startdate',
                    itemId: 'startdateid',
                    format: 'Y-m-d',
                    style: 'width:100%',
                    labelStyle : "text-align:right;padding-top: 6px;",
                    maxValue: new Date(),
                    margin : '10 0 0 20',
                    hidden:true,
                    listeners: {
                        //展开开始日期窗口，关闭结束日期窗口
                        expand: function (field) {
                            var endday = this.findParentByType('ReportSearchView').down('[itemId = enddateid]');
                            endday.collapse();
                        },
                        select: function (datefield, date) {
                            var endday = this.findParentByType('ReportSearchView').down('[itemId = enddateid]');
                            // endday.setMinValue(date);
                            Ext.defer(function () {
                                endday.expand();
                            }, 10);
                        }
                    }
                },
                {
                    columnWidth: .49,
                    emptyText: '请选择结束日期',
                    fieldLabel: '结束日期',
                    xtype: 'datefield',
                    name: 'enddate',
                    itemId: 'enddateid',
                    format: 'Y-m-d',
                    style: 'width:100%',
                    labelStyle : "text-align:right;padding-top: 6px;",
                    margin : '10 20 0 20',
                    hidden:true,
                },
            ],
            buttons:[{
                text:'开始统计',
                itemId:'bottomSearchBtn'
            }]
        },
        {
            itemId: 'reportviewId',
            region: 'center',
            width: '100%',
            height: '80%',
            title:'',
            html:'<div id="loadingDiv" style="display: none; "><div id="over" style=" position: absolute;top: 0;left: 0; width: 100%;height: 100%; background-color: #f5f5f5;opacity:0.5;z-index: 1000;"></div><div id="layout" style="position: absolute;left: 35%; z-index: 1001;text-align:center;"><img src="../img/Picloading.gif" /></div></div>'+
            '<iframe id="iframeId" src= "" frameborder="0" style="width: 100%;height: 100%"></iframe>',
        },
    ]
});