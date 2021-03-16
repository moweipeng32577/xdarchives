/**
 * Created by wangmh on 2019/4/30.
 */
//ureport报表搜索栏（默认带有开始时间与结束时间，需要其他控件的同学们自己添加）

var lyModeStore = Ext.create("Ext.data.Store", {
    fields: ["text", "value"],
    data: [
        { text: "馆藏总量一览表", value: "馆藏总量一览表"},
        { text: "馆藏档案统计表", value: "馆藏档案统计表"},
        { text: "年度馆藏文书一览表", value: "年度馆藏文书一览表" },
        { text: "全宗档案总数一览表", value: "全宗档案总数一览表"},
        { text: "年度馆藏专门档案一览表", value: "年度馆藏专门档案一览表" },
        { text: "档案馆开放档案统计台帐", value: "档案馆开放档案统计台帐"},
        { text: "档案馆鉴定工作统计表", value: "档案馆鉴定工作统计表" },
        { text: "档案利用统计表", value: "档案利用统计表"},
        { text: "预约统计表", value: "预约统计表"},
        { text: "评分统计表", value: "评分统计表"}
    ]
});

var dataStore = Ext.create("Ext.data.Store", {
    fields: ["text", "value"],
    data: [
        { text: "采集库", value: "采集库"},
        { text: "管理库", value: "管理库" }
    ]
});

var openStore = Ext.create("Ext.data.Store", {
    fields: ["text", "value"],
    data: [
        { text: "条目开放", value: "条目开放"}
    ]
});

var eleStore = Ext.create("Ext.data.Store", {
    fields: ["text", "value"],
    data: [
        { text: "jpg", value: "jpg"},
        { text: "bmp", value: "bmp" },
        { text: "jpeg", value: "jpeg" },
        { text: "png", value: "png" },
        { text: "pdf", value: "pdf" },
        { text: "doc", value: "doc" },
        { text: "docx", value: "docx" },
        { text: "xlsm", value: "xlsm" },
        { text: "xlsx", value: "xlsx" },
        { text: "ppt", value: "ppt" },
        { text: "pptx", value: "pptx" },
        { text: "tif", value: "tif" },
    ]
});

var entryretentionStore = Ext.create("Ext.data.Store", {
    fields: ["text", "value"],
    data: [
        { text: "10年", value: "10年"},
        { text: "30年", value: "30年" },
        { text: "短期", value: "短期" },
        { text: "长期", value: "长期" },
        { text: "永久", value: "永久" }
    ]
});

Ext.define('ReportSearch.view.ReportSearchView',{
    extend: 'Ext.form.Panel',
    xtype: 'ReportSearchView',
    layout:'border',
    items:[
        {
            xtype:'form',
            layout:'column',
            region: 'north',
            title:'统计条件栏',
            collapsible:true,
            height: '45%',
            collapsible: true,   // make collapsible
            split: true,         // enable resizing
            items: [
                {
                    columnWidth: 0.49,
                    style: 'width:100%',
                    fieldLabel: '数据来源',
                    itemId: 'datasource',
                    name:'datasource',
                    xtype: 'combo',
                    emptyText: '请选择',
                    queryMode: 'local',
                    editable: true,
                    margin : '10 0 0 20',
                    store:dataStore,
                    hidden:true,
                    valueField: 'text',
                    displayField: 'value',
                    triggerAction: 'all',
                    listeners : {
                        afterRender: function (combo) {
                            combo.setValue("管理库");//同时下拉框会将与name为firstValue值对应的 text显示
                        }
                    }
                }, {
                    columnWidth: .49,
                    style: 'width:100%',
                    fieldLabel: '保管期限',
                    itemId: 'entryretentionId',
                    multiSelect: true,
                    name:'entryretention',
                    xtype: 'combo',
                    emptyText: '请选择',
                    queryMode: 'local',
                    editable: false,
                    margin : '10 20 0 20',
                    hidden:true,
                    store:entryretentionStore,
                    autoLoad: true,
                    valueField: 'value',
                    displayField: 'text',
                    triggerAction: 'all',
                },{
                    columnWidth: 0.49,
                    style: 'width:90%',
                    fieldLabel: '开放程度',
                    itemId: 'flagopen',
                    name:'flagopen',
                    xtype: 'combo',
                    emptyText: '请选择',
                    queryMode: 'local',
                    editable: true,
                    margin : '10 0 0 20',
                    store:openStore,
                    hidden:true,
                    valueField: 'text',
                    displayField: 'value',
                    triggerAction: 'all',
                },{
                    columnWidth: .49,
                    style: 'width:100%',
                    width: '90%',
                    xtype : 'combo',
                    store : lyModeStore ,
                    name:'reportType',
                    fieldLabel: '台账报表类型',
                    itemId:'reporttypeId',
                    displayField : 'text',
                    allowBlank:false,
                    valueField : 'value',
                    editable:false,
                    margin:'10 0 0 20',
                    hidden:true,
                    listeners:{
                        afterrender:function(combo){
                            var store = combo.getStore();
                            if(store.getCount() > 0){
                                combo.select(store.getAt(0));
                                combo.findParentByType('ReportSearchView').down('[itemId=filingyeartype]').hide();
                                combo.findParentByType('ReportSearchView').down('[itemId=reportfundsId]').hide();
                            }
                        },
                        select:function (combo) {
                            combo.findParentByType('ReportSearchView').down('[itemId=classId]').hide();
                            combo.findParentByType('ReportSearchView').down('[itemId=startdateid]').hide();
                            combo.findParentByType('ReportSearchView').down('[itemId=enddateid]').hide();
                            var  selectcombo =combo.lastValue;
                            if(selectcombo == '全宗档案总数一览表'){
                                combo.findParentByType('ReportSearchView').down('[itemId=reportfundsId]').show();
                                combo.findParentByType('ReportSearchView').down('[itemId=filingyeartype]').hide();
                            }
                            else if( selectcombo =='档案利用统计表' || selectcombo == '档案馆鉴定工作统计表'){
                                combo.findParentByType('ReportSearchView').down('[itemId=reportfundsId]').hide();
                                combo.findParentByType('ReportSearchView').down('[itemId=filingyeartype]').show();
                            }
                            else if( selectcombo =='预约统计表' || selectcombo =='评分统计表'){
                                combo.findParentByType('ReportSearchView').down('[itemId=startdateid]').show();
                                combo.findParentByType('ReportSearchView').down('[itemId=enddateid]').show();
                            }else if(selectcombo =='馆藏档案统计表'){
                                var classCombo= combo.findParentByType('ReportSearchView').down('[itemId=classId]');
                                classCombo.show();
                                Ext.Ajax.request({
                                    url: '/reportmanagement/getEntryIndexClassName',
                                    method: 'GET',
                                    timeout: 100000,
                                    scope: this,
                                    success: function (resp) {
                                        var data=Ext.decode(resp.responseText);
                                        classCombo.bindStore(data);//更新下拉框
                                    }
                                });
                                combo.findParentByType('ReportSearchView').down('[itemId=reportfundsId]').show();
                                combo.findParentByType('ReportSearchView').down('[itemId=filingyeartype]').show();
                            }else{
                                combo.findParentByType('ReportSearchView').down('[itemId=reportfundsId]').hide();
                                combo.findParentByType('ReportSearchView').down('[itemId=filingyeartype]').hide();
                            }
                        }
                    }
                }, {
                    columnWidth: .49,
                    style: 'width:100%',
                    xtype : 'combobox',
                    store : [] ,
                    name:'className',
                    fieldLabel: '门类',
                    emptyText: '请选择门类类型',
                    itemId:'classId',
                    margin:'10 20 0 20',
                    hidden:true,
                    editable: false,
                    listeners: {
                        select: function (combo) {
                            var value=combo.getValue();
                            var fundsCombo= combo.findParentByType('ReportSearchView').down('[itemId=reportfundsId]');
                            var YearCombo= combo.findParentByType('ReportSearchView').down('[itemId=filingyeartype]');
                            Ext.Ajax.request({
                                url: '/reportmanagement/getEntryIndexFunds',
                                method: 'GET',
                                params: {className:value},
                                timeout: 100000,
                                scope: this,
                                success: function (resp) {
                                    var data=Ext.decode(resp.responseText);
                                    fundsCombo.select("");
                                    YearCombo.select("");
                                    YearCombo.bindStore([]);
                                    fundsCombo.bindStore(data);//更新下拉框
                                }
                            });
                        }
                    }
                },{
                    columnWidth: .49,
                    style: 'width:100%',
                    xtype : 'combobox',
                    store :'ReportFundsStore' ,
                    name:'funds',
                    fieldLabel: '全宗',
                    emptyText: '请选择全宗信息',
                    itemId:'reportfundsId',
                    displayField: "fundsname",
                    margin:'10 0 0 20',
                    hidden:true,
                    editable: false,
                    listeners: {
                        select: function (combo) {
                            var classIdCombo = combo.findParentByType('ReportSearchView').down('[itemId=classId]');
                            var YearCombo = combo.findParentByType('ReportSearchView').down('[itemId=filingyeartype]');
                            Ext.Ajax.request({
                                url: '/reportmanagement/getEntryIndexFilingYear',
                                method: 'GET',
                                params: {className: classIdCombo.getValue(), funds: combo.getValue()},
                                timeout: 100000,
                                scope: this,
                                success: function (resp) {
                                    YearCombo.select("");
                                    var data = Ext.decode(resp.responseText);
                                    var newData=[];
                                    newData[0]="-- 全选 --";
                                    newData=newData.concat(data);
                                    newData[newData.length]="-- 全不选 --";
                                    YearCombo.bindStore(newData);//更新下拉框
                                }
                            });
                        }
                    }
                },{
                    columnWidth: .49,
                    style: 'width:100%',
                    fieldLabel: '归档年度',
                    itemId: 'filingyeartype',
                    multiSelect: true,
                    xtype: 'combo',
                    emptyText: '请选择',
                    queryMode: 'local',
                    editable: false,
                    margin : '10 20 0 20',
                    hidden:true,
                    store: {
                        proxy: {
                            type: 'ajax',
                            extraParams: {},
                            url: '/reportmanagement/getFilingyeartype',
                            reader: {
                                type: 'json'
                            }
                        },
                        autoLoad: true,
                        listeners: {
                            load: function (s) {
                                s.insert(0, {
                                    "id": "noselect",
                                    "name": "<span style='color: #787878;display: block;text-align:center'>-- 不选 --</span>"
                                });
                                s.insert(s.totalCount + 1, {
                                    "id": "selectall",
                                    "name": "<span style='color: #787878;display: block;text-align:center'>-- 全选 --</span>"
                                })
                            }
                        }
                    },
                    valueField: 'id',
                    displayField: 'name',
                    triggerAction: 'all',
                    listeners: {
                        select: function (view, record) {
                            var combo = view.up('ReportSearchView').down('[itemId=filingyeartype]');
                            if (record.length > 0) {
                                var lastSelect = record[record.length - 1].get('id');
                                if (lastSelect === 'noselect') {
                                    combo.getStore().getAt(0).style = 'color:white;'
                                    combo.select(null);
                                } else if (lastSelect === 'selectall') {
                                    var newStore = combo.getStore().getData().items;
                                    var item = [];
                                    for (var j = 0; j < newStore.length; j++) {
                                        if (j !== 0 && j != newStore.length - 1) {
                                            item.push(newStore[j]);
                                        }
                                        else{
                                            combo.getStore().getAt(0).style = 'color:white;'
                                        }
                                    }
                                    combo.select(item);
                                }
                            }
                        }
                    }
                },{
                    columnWidth: .49,
                    style: 'width:100%',
                    fieldLabel: '电子文件类型',
                    itemId: 'eletype',
                    multiSelect: true,
                    name:'eletype',
                    xtype: 'combo',
                    emptyText: '请选择',
                    queryMode: 'local',
                    editable: false,
                    margin : '10 20 0 20',
                    hidden:true,
                    store:eleStore,
                    autoLoad: true,
                    valueField: 'value',
                    displayField: 'text',
                    triggerAction: 'all',
                },{
                    columnWidth: .49,
                    fieldLabel: '开始日期',
                    emptyText: '请选择开始日期',
                    xtype: 'datefield',
                    name: 'startdate',
                    itemId: 'startdateid',
                    format: 'Y-m-d',
                    style: 'width:100%',
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
                }, {
                    columnWidth: .49,
                    emptyText: '请选择结束日期',
                    fieldLabel: '结束日期',
                    xtype: 'datefield',
                    name: 'enddate',
                    itemId: 'enddateid',
                    format: 'Y-m-d',
                    style: 'width:100%',
                    margin : '10 20 0 20',
                    hidden:true,
                    listeners: {
                        select: function (datefield, date) {
                        }
                    }
                },{
                    columnWidth:.40,
                    xtype: 'TreeComboboxView',
                    itemId: 'classifyId',
                    fieldLabel: '档案分类',
                    name: 'classifyId',
                    editable: false,
                    margin : '10 0 0 20',
                    emptyText: '请选择',
                    hidden:true,
                    url: '/nodesetting/getCheckedClassificationByParentClassId',
                    extraParams: {pcid:''},//根节点的ParentNodeID为空，故此处传入参数为空串
                },{
                    columnWidth:.09,
                    xtype:'button',
                    text : '清空',
                    itemId:'classClearId',
                    hidden:true,
                    margin : '10 0 0 0',
                    hidden:true,
                    listeners: {
                        click: function (view) {
                            var classifyview = this.findParentByType('ReportSearchView').down('[itemId = classifyId]');
                            classifyview.setValue("");
                        }
                    }
                }, {
                    columnWidth:.40,
                    xtype: 'TreeComboboxView',
                    itemId: 'organId',
                    fieldLabel: '单位',
                    name: 'organId',
                    editable: false,
                    emptyText: '请选择',
                    margin : '10 0 0 20',
                    hidden:true,
                    url: '/nodesetting/getCheckedOrganByParentId',
                    extraParams: {pcid:''},//根节点的ParentNodeID为空，故此处传入参数为空串
                }, {
                    columnWidth:.08,
                    xtype:'button',
                    text : '清空',
                    itemId:'organClearId',
                    hidden:true,
                    margin : '10 0 0 0',
                    hidden:true,
                    listeners: {
                        click: function (view) {
                            var organview = this.findParentByType('ReportSearchView').down('[itemId = organId]');
                            organview.setValue("");
                        }
                    }
                }, {
                    itemId:'tipId',
                    columnWidth:.50,
                    margin : '10 0 0 0',
                    hidden:true,
                    html:'<div style="color: red;margin: 10px 10px 10px 10px;">温馨提示：若没有选择条件，则统计全部数据。</div>'
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
            height: '80%',
            title:'',
            html:'<div id="loadingDiv" style="display: none; "><div id="over" style=" position: absolute;top: 0;left: 0; width: 100%;height: 100%; background-color: #f5f5f5;opacity:0.5;z-index: 1000;"></div><div id="layout" style="position: absolute;left: 35%; z-index: 1001;text-align:center;"><img src="../img/Picloading.gif" /></div></div>'+
            '<iframe id="iframeId" src= "" frameborder="0" style="width: 100%;height: 100%"></iframe>',
        },
    ]
});