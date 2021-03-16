/**
 * Created by wujy on 2019/7/25.
 */

/*var lyModeStore = Ext.create("Ext.data.Store", {
    fields: ["text", "value"],
    data: [
        { text: "馆藏总量一览表", value: "馆藏总量一览表"},
        { text: "年度馆藏文书一览表", value: "年度馆藏文书一览表" },
        { text: "全宗档案总数一览表", value: "全宗档案总数一览表"},
        { text: "年度馆藏专门档案一览表", value: "年度馆藏专门档案一览表" },
        { text: "档案馆开放档案统计台帐", value: "档案馆开放档案统计台帐"},
        { text: "档案馆鉴定工作统计表", value: "档案馆鉴定工作统计表" },
        { text: "档案利用统计表", value: "档案利用统计表"}
    ]
});*/

Ext.define('ReportManage.view.ReportSearchView',{
    extend: 'Ext.form.Panel',
    xtype: 'ReportSearchView',
    layout:'border',
    items:[
        {
            xtype:'form',
            layout:'column',
            region: 'north',
            title:'搜索栏',
            collapsible:true,
            height: '30%',
            split: true,         // enable resizing
            items: [
                /*{
                    columnWidth: .47,
                    style: 'width:100%',
                    xtype : 'combo',
                    store : lyModeStore ,
                    name:'reportType',
                    fieldLabel: '统计报表类型',
                    itemId:'reporttypeId',
                    displayField : 'text',
                    allowBlank:false,
                    valueField : 'value',
                    editable:false,
                    margin:'10 0 0 20',
                    listeners:{
                        afterrender:function(combo){
                            var store = combo.getStore();
                            if(store.getCount() > 0){
                                combo.select(store.getAt(0));
                            }
                        }
                    }
                },*/
                {
                    columnWidth: .47,
                    fieldLabel: '开始日期',
                    xtype: 'datefield',
                    name: 'startdate',
                    itemId: 'startdateid',
                    format: 'Y-m-d',
                    style: 'width:100%',
                    maxValue: new Date(),
                    margin : '10 0 0 20',
                    listeners: {
                        //展开开始日期窗口，关闭结束日期窗口
                        expand: function (field) {
                            var endday = this.findParentByType('ReportSearchView').down('[itemId = enddateid]');
                            endday.collapse();
                        },
                        select: function (datefield, date) {
                            var endday = this.findParentByType('ReportSearchView').down('[itemId = enddateid]');
                            endday.setMinValue(date);
                            Ext.defer(function () {
                                endday.expand();
                            }, 10);
                        }
                    }
                }, {
                    columnWidth: .06,
                    xtype: 'displayfield'
                }, {
                    columnWidth: .47,
                    fieldLabel: '结束日期',
                    xtype: 'datefield',
                    name: 'enddate',
                    itemId: 'enddateid',
                    format: 'Y-m-d',
                    style: 'width:100%',
                    margin : '10 20 0 20',
                    listeners: {
                        select: function (datefield, date) {
                        }
                    }
                },{
                    columnWidth:.47,
                    xtype: 'combo',
                    itemId: 'waretypeId',
                    fieldLabel: '出库类型',
                    name: 'waretype',
                    editable: true,
                    margin : '10 0 0 20',
                    emptyText: '请选择',
                    store:new Ext.data.Store({
                        fields:['date','operation'],
                        data:[
                            {code:'调档出库',name:'调档出库'},
                            {code:'查档出库',name:'查档出库'},
                            {code:'转递出库',name:'转递出库'}
                        ]
                    }),
                    displayField : 'name',
                    allowBlank:false,
                    valueField : 'code'
                },{
                    columnWidth:.47,
                    xtype: 'TreeComboboxView',
                    itemId: 'classifyId',
                    fieldLabel: '分类',
                    name: 'classifyId',
                    editable: false,
                    margin : '10 0 0 20',
                    emptyText: '请选择',
                    url: '/nodesetting/getCheckedClassificationByParentClassId',
                    extraParams: {pcid:''}//根节点的ParentNodeID为空，故此处传入参数为空串
                },{
                    columnWidth: .60,
                    xtype: 'displayfield'
                },{
                    itemId:'tipId',
                    columnWidth:.50,
                    html:'<div id="hint" style="color: red;margin: 10px 10px 10px 10px;">温馨提示：若没有选择日期或档案类型，则统计全部数据。</div>'
                }
            ],
            buttons:[{
                text:'开始统计',
                itemId:'bottomSearchBtn'
            },'-',{
                text:'关闭',
                itemId:'bottomCloseBtn',
                handler:function(){
                    parent.closeObj.close(parent.layer.getFrameIndex(window.name));
                }
            }]
        },
        {
            itemId: 'reportviewId',
            region: 'center',
            width: '100%',
            height: '70%',
            title:'',
            html:'<div id="loadingDiv" style="display: none; "><div id="over" style=" position: absolute;top: 0;left: 0; width: 100%;height: 100%; background-color: #f5f5f5;opacity:0.5;z-index: 1000;"></div><div id="layout" style="position: absolute;top: 10%; left: 35%;width: 10%; height: 10%;  z-index: 1001;text-align:center;"><img src="../img/Picloading.gif" /></div></div>'+
            '<iframe id="iframeId" src= "" frameborder="0" style="width: 100%;height: 100%"></iframe>',
        }
    ]
});