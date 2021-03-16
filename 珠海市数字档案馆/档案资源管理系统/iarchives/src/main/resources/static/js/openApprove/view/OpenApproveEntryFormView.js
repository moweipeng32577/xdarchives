/**
 * Created by Administrator on 2020/7/30.
 */

var openStore = Ext.create("Ext.data.Store", {
    fields: ["Name", "Value"],
    data: [
        {text:"不选",Value:""},
        { text: "原文开放", Value: "原文开放" },
        { text: "条目开放", Value: "条目开放" },
        { text: "不开放", Value: "不开放"}

    ]
});

var LastopenStore = Ext.create("Ext.data.Store", {
    fields: ["Name", "Value"],
    data: [{text:"不选",Value:""},
        { text: "原文开放", Value: "原文开放" },
        { text: "条目开放", Value: "条目开放" },
        { text: "不开放", Value: "不开放"}

    ]
});

Ext.define('OpenApprove.view.OpenApproveEntryFormView',{
    extend:'Ext.tab.Panel',
    xtype:'openApproveEntryFormView',
    itemId:'openApproveEntryFormViewId',
    //标签页靠左配置--start
    tabPosition:'top',
    tabRotation:0,
    //标签页靠左配置--end

    activeTab:0,

    items:[{
        title:'条目',
        iconCls: 'x-tab-entry-icon',
        xtype:'panel',
        itemId:'entryPanel',
        layout:'border',
        items:[{
            itemId:'dynamicform',
            xtype:'dynamicform',
            height:'65%',
            region: 'north',
            //设置默认calurl（数据审核模块initFormField方法中已对该属性重新赋值为/acquisition/getCalValue，其它模块采用默认url设置）
            //不使用此公共组件时，自行在对应视图中设置calurl属性的值
            calurl:'/management/getCalValue',
            items:[{
                xtype:'hidden',
                name:'entryid'
            }]
        },{
            region:'center',
            xtype:'fieldset',
            itemId:'fieldsetFormId',
            height:'35%',
            layout: 'column',
            items: [{
                columnWidth: .23,
                xtype: "combobox",
                fieldLabel: "拟开放状态",
                name:'firstresult',
                store: openStore,
                editable: false,
                margin:'10 0 0 0',
                readOnly:approveText=='Fs'? false:true,
                displayField: "text",
                valueField: "Value",
                queryMode: "local",
                trigger1Cls:'x-form-clear-trigger',
                listeners:{
                    select:function (view,record) {
                        var openApproveEntryFormView = view.findParentByType('openApproveEntryFormView');
                        var entryunit = openApproveEntryFormView.down('[itemId=entryunitId]');
                        var appraisedata = openApproveEntryFormView.down('[itemId=appraisedataId]');
                        var appraisetext=openApproveEntryFormView.down('[itemId=appraisetextId]');
                        if(record.get('text')=='不开放'){
                            entryunit.setReadOnly(true);
                            appraisedata.setReadOnly(false);
                        }else if(approveText=='Fs'&&(record.get('text')=='条目开放'||record.get('text')=='原文开放')){
                            openApproveEntryFormView.down('[itemId=appraisedataId]').setValue("");
                            entryunit.setReadOnly(false);
                            appraisedata.setReadOnly(false);
                            appraisetext.setReadOnly(false)
                        }else if(approveText=='Fs'&&record.get('text')=='不选'){
                            openApproveEntryFormView.down('[itemId=appraisedataId]').setValue("");
                            openApproveEntryFormView.down('[itemId=entryunitId]').setValue("");
                            openApproveEntryFormView.down('[itemId=appraisetextId]').setValue("");
                            entryunit.setReadOnly(true);
                            appraisedata.setReadOnly(true);
                            appraisetext.setReadOnly(true)
                        }else{
                            openApproveEntryFormView.down('[itemId=entryunitId]').setValue("");
                            entryunit.setReadOnly(false);
                            appraisedata.setReadOnly(true);
                        }

                        if(record.get('text')!='不选'){
                            openApproveEntryFormView.down('[itemId=appraisetextId]').setValue(record.get("Value"));
                        }

                    }
                }
            }, {
                columnWidth: .01,
                xtype: 'displayfield'
            }, {
                columnWidth: .23,
                xtype: "combobox",
                fieldLabel: "复审开放状态",
                name:'lastresult',
                store: LastopenStore,
                editable: false,
                readOnly:approveText=='Ls'? false:true,
                margin:'10 0 0 0',
                displayField: "text",
                valueField: "Value",
                queryMode: "local",
                listeners:{
                    select:function (view,record) {
                        var openApproveEntryFormView = view.findParentByType('openApproveEntryFormView');
                        var entryunit = openApproveEntryFormView.down('[itemId=entryunitId]');
                        var appraisedata = openApproveEntryFormView.down('[itemId=appraisedataId]');
                        if(record.get('text')=='不开放'){
                            entryunit.setReadOnly(true);
                            appraisedata.setReadOnly(false);
                        }else  if(approveText=='Ls'&&record.get('text')=='不选') {
                            // openApproveEntryFormView.down('[itemId=appraisedataId]').setValue("");
                            // openApproveEntryFormView.down('[itemId=entryunitId]').setValue("");
                            openApproveEntryFormView.down('[itemId=lastappraisetextId]').setValue("");
                            entryunit.setReadOnly(false);
                            appraisedata.setReadOnly(true);
                        }
                        else{
                            entryunit.setReadOnly(false);
                            appraisedata.setReadOnly(true);
                        }
                    }
                }
            }, {
                columnWidth: .06,
                xtype: 'displayfield'
            }, {
                columnWidth: .47,
                xtype: "combobox",
                fieldLabel: "档案所属单位",
                itemId:'entryunitId',
                name:'entryunit',
                store:Ext.create('Ext.data.Store', {
                    proxy: {
                        type: 'ajax',
                        url: '/systemconfig/enums',
                        extraParams: {
                            value:'entryUnitType'
                        },
                        reader: {
                            type: 'json'
                        }
                    },
                    autoLoad: true
                }),
                displayField:'value',
                valueField:'value',
                labelWidth: 100,
                editable: false,
                margin:'10 0 0 0',
                queryMode: "local"
            },{
                columnWidth: 1,
                xtype: "combobox",
                fieldLabel: "鉴定依据",
                itemId:'appraisedataId',
                name:'appraisedata',
                store:Ext.create('Ext.data.Store', {
                    proxy: {
                        type: 'ajax',
                        url: '/systemconfig/enums',
                        extraParams: {
                            value:'openAppraiseType'
                        },
                        reader: {
                            type: 'json'
                        }
                    },
                    autoLoad: true
                }),
                displayField:'value',
                valueField:'value',
                labelWidth: 100,
                editable: false,
                margin:'10 0 0 0',
                queryMode: "local"
            },{
                columnWidth: .23,
                fieldLabel: '初审鉴定意见',
                xtype: 'textfield',
                name: 'appraisetext',
                itemId:'appraisetextId',
                readOnly:approveText=='Fs'? false:true,
                margin:'10 0 0 0',
                labelWidth: 100
            }, {
                columnWidth: .01,
                xtype: 'displayfield'
            }, {
                columnWidth:.23,
                fieldLabel: '初审人',
                xtype: 'textfield',
                name: 'firstappraiser',
                itemId:'firstappraiserId',
                editable: false,
                margin:'10 0 0 0',
                labelWidth: 100
            }, {
                columnWidth: .06,
                xtype: 'displayfield'
            }, {
                columnWidth:.23,
                fieldLabel: '复审鉴定意见',
                xtype: 'textfield',
                name: 'lastappraisetext',
                itemId:'lastappraisetextId',
                readOnly:approveText=='Ls'? false:true,
                margin:'10 0 0 0',
                labelWidth: 100
            }, {
                columnWidth: .01,
                xtype: 'displayfield'
            }, {
                columnWidth:.23,
                fieldLabel: '复审人',
                xtype: 'textfield',
                name: 'lastappraiser',
                itemId:'lastappraiserId',
                editable: false,
                margin:'10 0 0 0',
                labelWidth: 100
            }, {
                columnWidth: 1,
                xtype: 'textfield',
                name: 'updatetitle',
                itemId:'updatetitleId',
                margin:'10 0 0 0',
                fieldLabel: '修改题名',
                labelWidth: 100,
                hidden:true
            }, {
                columnWidth: 1,
                xtype: "combobox",
                fieldLabel: "最终鉴定结果",
                name:'finalresult',
                store: openStore,
                editable: false,
                readOnly:approveText=='Jd'? false:true,
                margin:'10 0 0 0',
                labelWidth: 100,
                displayField: "text",
                valueField: "Value",
                queryMode: "local"
            }]
        }]
    },{
        title:'原始文件',
        iconCls:'x-tab-electronic-icon',
        itemId:'electronic',
        entrytype:'electronic',
        xtype:'electronic'
    },{
        title:'利用文件',
        iconCls:'x-tab-electronic-icon',
        itemId:'solid',
        entrytype:'solid',
        xtype:'solid'
    }],

    buttons:[{
        xtype: 'textfield',
        name: 'updatetitle',
        itemId:'updatetitleId2',
        fieldLabel: '修改题名',
        labelWidth: 100,
        width: "90%",
        margin: '0 80 0 0'
    },{
        text:'保存',
        itemId:'saveId'
    },{
        text:'返回',
        itemId:'back'
    }]
});
