/**
 * Created by Leo on 2020/7/6 0006.
 */
Ext.define('ConsultStandingBook.view.StandingBookManagementView', {
    extend: 'Ext.form.Panel',
    xtype: 'standingBookManagementView',
    itemId: 'standingBookManagementViewId',
    region: 'center',
    autoScroll: true,
    fieldDefaults: {
        //labelWidth: 120
    },
    //layout: 'column',
    bodyPadding: 15,
    tbar: [{
        xtype: 'datefield',
        name: 'consultDate',
        itemId: 'consultDateId',
        labelStyle : "text-align:right;width:100;padding-top:4px",
        fieldLabel: '查档日期',
        format: 'Y-m-d',
        editable: false,
        value:new Date().format('yyyy-MM-dd'),
        maxValue: new Date(),
        listeners:{
            select: function (datefield, date) {
                var view=datefield.findParentByType('standingBookManagementView');
                var dateTime=Ext.util.Format.date(datefield.getValue(), 'Y-m-d');
                loadConsultStatistics(view,dateTime);
            }
        }
    },{
        itemId: 'statistics',
        xtype: 'button',
        text: '统计'
    }, {
        itemId: 'save',
        xtype: 'button',
        text: '保存'
    }, '-', {
        itemId: 'delete',
        xtype: 'button',
        text: '删除'
    }],
    items: [{
        layout: 'column',
        itemId:'wsFormId',
        items: [{
            columnWidth:.11,
            xtype: 'label',
            name:'typeName',
            text: '文书档案：',
            margin:'7 0 0 0',
        },{
            xtype: 'textfield',
            name: 'type',
            hidden: true,
            value:'文书档案'
        },{
            columnWidth: .16,
            xtype: 'numberfield',
            labelStyle : "text-align:right;width:100;padding-top: 7px;",
            fieldLabel: '单位（人次）',
            name: 'company',
            labelWidth: 100,
            allowBlank: false,
            allowDecimals:false,
            regex: /^([0-9]\d*)$/,
            regexText : '请输入正整数',
            value: 0
        },{
            columnWidth: .16,
            xtype: 'numberfield',
            labelStyle : "text-align:right;width:100;padding-top: 7px;",
            fieldLabel: '个人（人次）',
            name: 'personal',
            labelWidth: 100,
            allowBlank: false,
            allowDecimals:false,
            regex: /^([0-9]\d*)$/,
            regexText : '请输入正整数',
            value: 0
        },{
            columnWidth: .14,
            xtype: 'numberfield',
            labelStyle : "text-align:right;width:100;padding-top: 7px;",
            fieldLabel: '卷',
            name: 'volume',
            labelWidth: 60,
            allowBlank: false,
            allowDecimals:false,
            regex: /^([0-9]\d*)$/,
            regexText : '请输入正整数',
            value: 0
        },{
            columnWidth: .14,
            xtype: 'numberfield',
            labelStyle : "text-align:right;width:100;padding-top: 7px;",
            fieldLabel: '件',
            name: 'piece',
            labelWidth: 60,
            allowDecimals:false,
            regex: /^([0-9]\d*)$/,
            regexText : '请输入正整数',
            allowBlank: false,
            value: 0
        },{
            columnWidth: .14,
            xtype: 'numberfield',
            labelStyle : "text-align:right;padding-top: 7px;",
            fieldLabel: '复印',
            name: 'tocopy',
            labelWidth:60,
            allowBlank: false,
            allowDecimals:false,
            regex: /^([0-9]\d*)$/,
            regexText : '请输入正整数',
            value: 0
        }, {
            columnWidth: .14,
            xtype: 'numberfield',
            labelStyle: "text-align:right;width:80;padding-top: 7px;",
            fieldLabel: '证明',
            name: 'prove',
            hidden:true,
            value: 0
        }]
    }, {
        layout: 'column',
        itemId: 'hyFormId',
        margin: '10 0 0 0',
        items: [{
            columnWidth:.11,
            xtype: 'label',
            name: 'type',
            text: '婚姻档案：',
            margin: '7 0 0 0',
        },{
            xtype: 'textfield',
            name: 'type',
            hidden: true,
            value:'婚姻档案'
        },{
            columnWidth: .16,
            xtype: 'numberfield',
            labelStyle : "text-align:right;width:100;padding-top: 7px;",
            fieldLabel: '单位（人次）',
            name: 'company',
            labelWidth: 100,
            allowBlank: false,
            allowDecimals:false,
            regex: /^([0-9]\d*)$/,
            regexText : '请输入正整数',
            value: 0
        },{
            columnWidth: .16,
            xtype: 'numberfield',
            labelStyle : "text-align:right;width:100;padding-top: 7px;",
            fieldLabel: '个人（人次）',
            name: 'personal',
            labelWidth: 100,
            allowBlank: false,
            allowDecimals:false,
            regex: /^([0-9]\d*)$/,
            regexText : '请输入正整数',
            value: 0
        },{
            columnWidth: .14,
            xtype: 'numberfield',
            labelStyle : "text-align:right;width:100;padding-top: 7px;",
            fieldLabel: '卷',
            name: 'volume',
            labelWidth: 60,
            allowBlank: false,
            allowDecimals:false,
            regex: /^([0-9]\d*)$/,
            regexText : '请输入正整数',
            value: 0
        },{
            columnWidth: .14,
            xtype: 'numberfield',
            labelStyle : "text-align:right;width:100;padding-top: 7px;",
            fieldLabel: '件',
            name: 'piece',
            labelWidth: 60,
            allowDecimals:false,
            regex: /^([0-9]\d*)$/,
            regexText : '请输入正整数',
            allowBlank: false,
            hidden:true,
            value: 0
        },{
            columnWidth: .14,
            xtype: 'numberfield',
            labelStyle : "text-align:right;padding-top: 7px;",
            fieldLabel: '复印',
            name: 'tocopy',
            labelWidth:60,
            allowBlank: false,
            allowDecimals:false,
            regex: /^([0-9]\d*)$/,
            regexText : '请输入正整数',
            value: 0
        }, {
            columnWidth: .14,
            xtype: 'numberfield',
            labelStyle: "text-align:right;width:80;padding-top: 7px;",
            fieldLabel: '证明',
            name: 'prove',
            labelWidth: 60,
            allowBlank: false,
            allowDecimals:false,
            regex: /^([0-9]\d*)$/,
            regexText : '请输入正整数',
            value: 0
        }]
    },{
        layout: 'column',
        itemId:'ryFormId',
        margin:'10 0 0 0',
        items: [{
            columnWidth: .11,
            xtype: 'label',
            name:'type',
            text: '人员/已故人员档案：',
            margin:'7 0 0 0',
        },{
            xtype: 'textfield',
            name: 'type',
            hidden: true,
            value:'人员/已故人员档案'
        },{
            columnWidth: .16,
            xtype: 'numberfield',
            labelStyle : "text-align:right;width:100;padding-top: 7px;",
            fieldLabel: '单位（人次）',
            name: 'company',
            labelWidth: 100,
            allowBlank: false,
            allowDecimals:false,
            regex: /^([0-9]\d*)$/,
            regexText : '请输入正整数',
            value: 0
        },{
            columnWidth: .16,
            xtype: 'numberfield',
            labelStyle : "text-align:right;width:100;padding-top: 7px;",
            fieldLabel: '个人（人次）',
            name: 'personal',
            labelWidth: 100,
            allowBlank: false,
            allowDecimals:false,
            regex: /^([0-9]\d*)$/,
            regexText : '请输入正整数',
            value: 0
        },{
            columnWidth: .14,
            xtype: 'numberfield',
            labelStyle : "text-align:right;width:100;padding-top: 7px;",
            fieldLabel: '卷',
            name: 'volume',
            labelWidth: 60,
            allowBlank: false,
            allowDecimals:false,
            regex: /^([0-9]\d*)$/,
            regexText : '请输入正整数',
            value: 0
        },{
            columnWidth: .14,
            xtype: 'numberfield',
            labelStyle : "text-align:right;width:100;padding-top: 7px;",
            fieldLabel: '件',
            name: 'piece',
            labelWidth: 60,
            allowDecimals:false,
            regex: /^([0-9]\d*)$/,
            regexText : '请输入正整数',
            allowBlank: false,
            value: 0
        },{
            columnWidth: .14,
            xtype: 'numberfield',
            labelStyle : "text-align:right;padding-top: 7px;",
            fieldLabel: '复印',
            name: 'tocopy',
            labelWidth:60,
            allowBlank: false,
            allowDecimals:false,
            regex: /^([0-9]\d*)$/,
            regexText : '请输入正整数',
            value: 0
        }, {
            columnWidth: .14,
            xtype: 'numberfield',
            labelStyle: "text-align:right;width:80;padding-top: 7px;",
            fieldLabel: '证明',
            name: 'prove',
            hidden:true,
            value: 0
        }]
    },{
        layout: 'column',
        itemId:'jjFormId',
        margin:'10 0 0 0',
        items: [{
            columnWidth:.11,
            xtype: 'label',
            name:'type',
            text: '科技/城建/基建档案：',
            margin:'7 0 0 0',
        },{
            xtype: 'textfield',
            name: 'type',
            hidden: true,
            value:'科技/城建/基建档案'
        },{
            columnWidth: .16,
            xtype: 'numberfield',
            labelStyle : "text-align:right;width:100;padding-top: 7px;",
            fieldLabel: '单位（人次）',
            name: 'company',
            labelWidth: 100,
            allowBlank: false,
            allowDecimals:false,
            regex: /^([0-9]\d*)$/,
            regexText : '请输入正整数',
            value: 0
        },{
            columnWidth: .16,
            xtype: 'numberfield',
            labelStyle : "text-align:right;width:100;padding-top: 7px;",
            fieldLabel: '个人（人次）',
            name: 'personal',
            labelWidth: 100,
            allowBlank: false,
            allowDecimals:false,
            regex: /^([0-9]\d*)$/,
            regexText : '请输入正整数',
            value: 0
        },{
            columnWidth: .14,
            xtype: 'numberfield',
            labelStyle : "text-align:right;width:100;padding-top: 7px;",
            fieldLabel: '卷',
            name: 'volume',
            labelWidth: 60,
            allowBlank: false,
            allowDecimals:false,
            regex: /^([0-9]\d*)$/,
            regexText : '请输入正整数',
            value: 0
        },{
            columnWidth: .14,
            xtype: 'numberfield',
            labelStyle : "text-align:right;width:100;padding-top: 7px;",
            fieldLabel: '件',
            name: 'piece',
            labelWidth: 60,
            allowDecimals:false,
            regex: /^([0-9]\d*)$/,
            regexText : '请输入正整数',
            allowBlank: false,
            hidden:true,
            value: 0
        },{
            columnWidth: .14,
            xtype: 'numberfield',
            labelStyle : "text-align:right;padding-top: 7px;",
            fieldLabel: '复印',
            name: 'tocopy',
            labelWidth:60,
            allowBlank: false,
            allowDecimals:false,
            regex: /^([0-9]\d*)$/,
            regexText : '请输入正整数',
            value: 0
        }, {
            columnWidth: .14,
            xtype: 'numberfield',
            labelStyle: "text-align:right;width:80;padding-top: 7px;",
            fieldLabel: '证明',
            name: 'prove',
            hidden:true,
            value: 0
        }]
    }, {
        layout: 'column',
        itemId: 'twFormId',
        margin:'10 0 0 0',
        items: [{
            columnWidth: .11,
            xtype: 'label',
            name: 'typeName',
            text: '退伍档案：',
            margin: '7 0 0 0',
        }, {
            xtype: 'textfield',
            name: 'type',
            hidden: true,
            value: '退伍档案'
        }, {
            columnWidth: .16,
            xtype: 'numberfield',
            labelStyle: "text-align:right;width:100;padding-top: 7px;",
            fieldLabel: '单位（人次）',
            name: 'company',
            labelWidth: 100,
            allowBlank: false,
            allowDecimals: false,
            regex: /^([0-9]\d*)$/,
            regexText: '请输入正整数',
            value: 0
        }, {
            columnWidth: .16,
            xtype: 'numberfield',
            labelStyle: "text-align:right;width:100;padding-top: 7px;",
            fieldLabel: '个人（人次）',
            name: 'personal',
            labelWidth: 100,
            allowBlank: false,
            allowDecimals: false,
            regex: /^([0-9]\d*)$/,
            regexText: '请输入正整数',
            value: 0
        }, {
            columnWidth: .14,
            xtype: 'numberfield',
            labelStyle: "text-align:right;width:100;padding-top: 7px;",
            fieldLabel: '卷',
            name: 'volume',
            labelWidth: 60,
            allowBlank: false,
            allowDecimals: false,
            regex: /^([0-9]\d*)$/,
            regexText: '请输入正整数',
            value: 0
        }, {
            columnWidth: .14,
            xtype: 'numberfield',
            labelStyle: "text-align:right;width:100;padding-top: 7px;",
            fieldLabel: '件',
            name: 'piece',
            labelWidth: 60,
            allowDecimals: false,
            regex: /^([0-9]\d*)$/,
            regexText: '请输入正整数',
            allowBlank: false,
            hidden:true,
            value: 0
        }, {
            columnWidth: .14,
            xtype: 'numberfield',
            labelStyle: "text-align:right;padding-top: 7px;",
            fieldLabel: '复印',
            name: 'tocopy',
            labelWidth: 60,
            allowBlank: false,
            allowDecimals: false,
            regex: /^([0-9]\d*)$/,
            regexText: '请输入正整数',
            value: 0
        }, {
            columnWidth: .14,
            xtype: 'numberfield',
            labelStyle: "text-align:right;width:80;padding-top: 7px;",
            fieldLabel: '证明',
            name: 'prove',
            hidden: true,
            value: 0
        }]
    }, {
        layout: 'column',
        itemId: 'tdFormId',
        margin:'10 0 0 0',
        items: [{
            columnWidth: .11,
            xtype: 'label',
            name: 'typeName',
            text: '土地档案：',
            margin: '7 0 0 0',
        }, {
            xtype: 'textfield',
            name: 'type',
            hidden: true,
            value: '土地档案'
        }, {
            columnWidth: .16,
            xtype: 'numberfield',
            labelStyle: "text-align:right;width:100;padding-top: 7px;",
            fieldLabel: '单位（人次）',
            name: 'company',
            labelWidth: 100,
            allowBlank: false,
            allowDecimals: false,
            regex: /^([0-9]\d*)$/,
            regexText: '请输入正整数',
            value: 0
        }, {
            columnWidth: .16,
            xtype: 'numberfield',
            labelStyle: "text-align:right;width:100;padding-top: 7px;",
            fieldLabel: '个人（人次）',
            name: 'personal',
            labelWidth: 100,
            allowBlank: false,
            allowDecimals: false,
            regex: /^([0-9]\d*)$/,
            regexText: '请输入正整数',
            value: 0
        }, {
            columnWidth: .14,
            xtype: 'numberfield',
            labelStyle: "text-align:right;width:100;padding-top: 7px;",
            fieldLabel: '卷',
            name: 'volume',
            labelWidth: 60,
            allowBlank: false,
            allowDecimals: false,
            regex: /^([0-9]\d*)$/,
            regexText: '请输入正整数',
            value: 0
        }, {
            columnWidth: .14,
            xtype: 'numberfield',
            labelStyle: "text-align:right;width:100;padding-top: 7px;",
            fieldLabel: '件',
            name: 'piece',
            labelWidth: 60,
            allowDecimals: false,
            regex: /^([0-9]\d*)$/,
            regexText: '请输入正整数',
            allowBlank: false,
            hidden:true,
            value: 0
        }, {
            columnWidth: .14,
            xtype: 'numberfield',
            labelStyle: "text-align:right;padding-top: 7px;",
            fieldLabel: '复印',
            name: 'tocopy',
            labelWidth: 60,
            allowBlank: false,
            allowDecimals: false,
            regex: /^([0-9]\d*)$/,
            regexText: '请输入正整数',
            value: 0
        }, {
            columnWidth: .14,
            xtype: 'numberfield',
            labelStyle: "text-align:right;width:80;padding-top: 7px;",
            fieldLabel: '证明',
            name: 'prove',
            hidden: true,
            value: 0
        }]
    }, {
        layout: 'column',
        itemId: 'lzFormId',
        margin:'10 0 0 0',
        items: [{
            columnWidth: .11,
            xtype: 'label',
            name: 'typeName',
            text: '林政档案：',
            margin: '7 0 0 0',
        }, {
            xtype: 'textfield',
            name: 'type',
            hidden: true,
            value: '林政档案'
        }, {
            columnWidth: .16,
            xtype: 'numberfield',
            labelStyle: "text-align:right;width:100;padding-top: 7px;",
            fieldLabel: '单位（人次）',
            name: 'company',
            labelWidth: 100,
            allowBlank: false,
            allowDecimals: false,
            regex: /^([0-9]\d*)$/,
            regexText: '请输入正整数',
            value: 0
        }, {
            columnWidth: .16,
            xtype: 'numberfield',
            labelStyle: "text-align:right;width:100;padding-top: 7px;",
            fieldLabel: '个人（人次）',
            name: 'personal',
            labelWidth: 100,
            allowBlank: false,
            allowDecimals: false,
            regex: /^([0-9]\d*)$/,
            regexText: '请输入正整数',
            value: 0
        }, {
            columnWidth: .14,
            xtype: 'numberfield',
            labelStyle: "text-align:right;width:100;padding-top: 7px;",
            fieldLabel: '卷',
            name: 'volume',
            labelWidth: 60,
            allowBlank: false,
            allowDecimals: false,
            regex: /^([0-9]\d*)$/,
            regexText: '请输入正整数',
            value: 0
        }, {
            columnWidth: .14,
            xtype: 'numberfield',
            labelStyle: "text-align:right;width:100;padding-top: 7px;",
            fieldLabel: '件',
            name: 'piece',
            labelWidth: 60,
            allowDecimals: false,
            regex: /^([0-9]\d*)$/,
            regexText: '请输入正整数',
            allowBlank: false,
            hidden:true,
            value: 0
        }, {
            columnWidth: .14,
            xtype: 'numberfield',
            labelStyle: "text-align:right;padding-top: 7px;",
            fieldLabel: '复印',
            name: 'tocopy',
            labelWidth: 60,
            allowBlank: false,
            allowDecimals: false,
            regex: /^([0-9]\d*)$/,
            regexText: '请输入正整数',
            value: 0
        }, {
            columnWidth: .14,
            xtype: 'numberfield',
            labelStyle: "text-align:right;width:80;padding-top: 7px;",
            fieldLabel: '证明',
            name: 'prove',
            hidden: true,
            value: 0
        }]
    }, {
        layout: 'column',
        itemId: 'htFormId',
        margin:'10 0 0 0',
        items: [{
            columnWidth: .11,
            xtype: 'label',
            name: 'typeName',
            text: '合同档案：',
            margin: '7 0 0 0',
        }, {
            xtype: 'textfield',
            name: 'type',
            hidden: true,
            value: '合同档案'
        }, {
            columnWidth: .16,
            xtype: 'numberfield',
            labelStyle: "text-align:right;width:100;padding-top: 7px;",
            fieldLabel: '单位（人次）',
            name: 'company',
            labelWidth: 100,
            allowBlank: false,
            allowDecimals: false,
            regex: /^([0-9]\d*)$/,
            regexText: '请输入正整数',
            value: 0
        }, {
            columnWidth: .16,
            xtype: 'numberfield',
            labelStyle: "text-align:right;width:100;padding-top: 7px;",
            fieldLabel: '个人（人次）',
            name: 'personal',
            labelWidth: 100,
            allowBlank: false,
            allowDecimals: false,
            regex: /^([0-9]\d*)$/,
            regexText: '请输入正整数',
            value: 0
        }, {
            columnWidth: .14,
            xtype: 'numberfield',
            labelStyle: "text-align:right;width:100;padding-top: 7px;",
            fieldLabel: '卷',
            name: 'volume',
            labelWidth: 60,
            allowBlank: false,
            allowDecimals: false,
            regex: /^([0-9]\d*)$/,
            regexText: '请输入正整数',
            value: 0
        }, {
            columnWidth: .14,
            xtype: 'numberfield',
            labelStyle: "text-align:right;width:100;padding-top: 7px;",
            fieldLabel: '件',
            name: 'piece',
            labelWidth: 60,
            allowDecimals: false,
            regex: /^([0-9]\d*)$/,
            regexText: '请输入正整数',
            allowBlank: false,
            hidden:true,
            value: 0
        }, {
            columnWidth: .14,
            xtype: 'numberfield',
            labelStyle: "text-align:right;padding-top: 7px;",
            fieldLabel: '复印',
            name: 'tocopy',
            labelWidth: 60,
            allowBlank: false,
            allowDecimals: false,
            regex: /^([0-9]\d*)$/,
            regexText: '请输入正整数',
            value: 0
        }, {
            columnWidth: .14,
            xtype: 'numberfield',
            labelStyle: "text-align:right;width:80;padding-top: 7px;",
            fieldLabel: '证明',
            name: 'prove',
            hidden: true,
            value: 0
        }]
    }, {
        layout: 'column',
        itemId: 'ywglFormId',
        margin:'10 0 0 0',
        items: [{
            columnWidth: .11,
            xtype: 'label',
            name: 'typeName',
            text: '业务/工龄档案：',
            margin: '7 0 0 0',
        }, {
            xtype: 'textfield',
            name: 'type',
            hidden: true,
            value: '业务/工龄档案'
        }, {
            columnWidth: .16,
            xtype: 'numberfield',
            labelStyle: "text-align:right;width:100;padding-top: 7px;",
            fieldLabel: '单位（人次）',
            name: 'company',
            labelWidth: 100,
            allowBlank: false,
            allowDecimals: false,
            regex: /^([0-9]\d*)$/,
            regexText: '请输入正整数',
            value: 0
        }, {
            columnWidth: .16,
            xtype: 'numberfield',
            labelStyle: "text-align:right;width:100;padding-top: 7px;",
            fieldLabel: '个人（人次）',
            name: 'personal',
            labelWidth: 100,
            allowBlank: false,
            allowDecimals: false,
            regex: /^([0-9]\d*)$/,
            regexText: '请输入正整数',
            value: 0
        }, {
            columnWidth: .14,
            xtype: 'numberfield',
            labelStyle: "text-align:right;width:100;padding-top: 7px;",
            fieldLabel: '卷',
            name: 'volume',
            labelWidth: 60,
            allowBlank: false,
            allowDecimals: false,
            regex: /^([0-9]\d*)$/,
            regexText: '请输入正整数',
            value: 0
        }, {
            columnWidth: .14,
            xtype: 'numberfield',
            labelStyle: "text-align:right;width:100;padding-top: 7px;",
            fieldLabel: '件',
            name: 'piece',
            labelWidth: 60,
            allowDecimals: false,
            regex: /^([0-9]\d*)$/,
            regexText: '请输入正整数',
            allowBlank: false,
            hidden:true,
            value: 0
        }, {
            columnWidth: .14,
            xtype: 'numberfield',
            labelStyle: "text-align:right;padding-top: 7px;",
            fieldLabel: '复印',
            name: 'tocopy',
            labelWidth: 60,
            allowBlank: false,
            allowDecimals: false,
            regex: /^([0-9]\d*)$/,
            regexText: '请输入正整数',
            value: 0
        }, {
            columnWidth: .14,
            xtype: 'numberfield',
            labelStyle: "text-align:right;width:80;padding-top: 7px;",
            fieldLabel: '证明',
            name: 'prove',
            hidden: true,
            value: 0
        }]
    }, {
        layout: 'column',
        itemId: 'qtzlFormId',
        margin:'10 0 0 0',
        items: [{
            columnWidth: .11,
            xtype: 'label',
            name: 'typeName',
            text: '其他档案/资料：',
            margin: '7 0 0 0',
        }, {
            xtype: 'textfield',
            name: 'type',
            hidden: true,
            value: '其他档案/资料'
        }, {
            columnWidth: .16,
            xtype: 'numberfield',
            labelStyle: "text-align:right;width:100;padding-top: 7px;",
            fieldLabel: '单位（人次）',
            name: 'company',
            labelWidth: 100,
            allowBlank: false,
            allowDecimals: false,
            regex: /^([0-9]\d*)$/,
            regexText: '请输入正整数',
            value: 0
        }, {
            columnWidth: .16,
            xtype: 'numberfield',
            labelStyle: "text-align:right;width:100;padding-top: 7px;",
            fieldLabel: '个人（人次）',
            name: 'personal',
            labelWidth: 100,
            allowBlank: false,
            allowDecimals: false,
            regex: /^([0-9]\d*)$/,
            regexText: '请输入正整数',
            value: 0
        }, {
            columnWidth: .14,
            xtype: 'numberfield',
            labelStyle: "text-align:right;width:100;padding-top: 7px;",
            fieldLabel: '卷',
            name: 'volume',
            labelWidth: 60,
            allowBlank: false,
            allowDecimals: false,
            regex: /^([0-9]\d*)$/,
            regexText: '请输入正整数',
            value: 0
        }, {
            columnWidth: .14,
            xtype: 'numberfield',
            labelStyle: "text-align:right;width:100;padding-top: 7px;",
            fieldLabel: '件',
            name: 'piece',
            labelWidth: 60,
            allowDecimals: false,
            regex: /^([0-9]\d*)$/,
            regexText: '请输入正整数',
            allowBlank: false,
            hidden:true,
            value: 0
        }, {
            columnWidth: .14,
            xtype: 'numberfield',
            labelStyle: "text-align:right;padding-top: 7px;",
            fieldLabel: '复印',
            name: 'tocopy',
            labelWidth: 60,
            allowBlank: false,
            allowDecimals: false,
            regex: /^([0-9]\d*)$/,
            regexText: '请输入正整数',
            value: 0
        }, {
            columnWidth: .14,
            xtype: 'numberfield',
            labelStyle: "text-align:right;width:80;padding-top: 7px;",
            fieldLabel: '证明',
            name: 'prove',
            hidden: true,
            value: 0
        }]
    }, {
        layout: 'column',
        itemId: 'dhxcFormId',
        margin:'10 0 0 0',
        items: [{
            columnWidth: .11,
            xtype: 'label',
            name: 'typeName',
            text: '电话/现场咨询：',
            margin: '7 0 0 0',
        }, {
            xtype: 'textfield',
            name: 'type',
            hidden: true,
            value: '电话/现场咨询'
        }, {
            columnWidth: .16,
            xtype: 'numberfield',
            labelStyle: "text-align:right;width:100;padding-top: 7px;",
            fieldLabel: '单位（人次）',
            name: 'company',
            labelWidth: 100,
            allowBlank: false,
            allowDecimals: false,
            regex: /^([0-9]\d*)$/,
            regexText: '请输入正整数',
            value: 0
        }, {
            columnWidth: .16,
            xtype: 'numberfield',
            labelStyle: "text-align:right;width:100;padding-top: 7px;",
            fieldLabel: '个人（人次）',
            name: 'personal',
            labelWidth: 100,
            allowBlank: false,
            allowDecimals: false,
            regex: /^([0-9]\d*)$/,
            regexText: '请输入正整数',
            value: 0
        }, {
            columnWidth: .14,
            xtype: 'numberfield',
            labelStyle: "text-align:right;width:100;padding-top: 7px;",
            fieldLabel: '卷',
            name: 'volume',
            labelWidth: 60,
            allowBlank: false,
            allowDecimals: false,
            regex: /^([0-9]\d*)$/,
            regexText: '请输入正整数',
            hidden:true,
            value: 0
        }, {
            columnWidth: .14,
            xtype: 'numberfield',
            labelStyle: "text-align:right;width:100;padding-top: 7px;",
            fieldLabel: '件',
            name: 'piece',
            labelWidth: 60,
            allowDecimals: false,
            regex: /^([0-9]\d*)$/,
            regexText: '请输入正整数',
            allowBlank: false,
            hidden:true,
            value: 0
        }, {
            columnWidth: .14,
            xtype: 'numberfield',
            labelStyle: "text-align:right;padding-top: 7px;",
            fieldLabel: '复印',
            name: 'tocopy',
            labelWidth: 60,
            allowBlank: false,
            allowDecimals: false,
            regex: /^([0-9]\d*)$/,
            regexText: '请输入正整数',
            hidden:true,
            value: 0
        }, {
            columnWidth: .14,
            xtype: 'numberfield',
            labelStyle: "text-align:right;width:80;padding-top: 7px;",
            fieldLabel: '证明',
            name: 'prove',
            hidden: true,
            hidden:true,
            value: 0
        }]
    }],
    buttons: [
        {text: '返回', itemId: 'managementClose'}
    ]
})