/**
 * Created by Administrator on 2017/10/23 0023.
 */
var passStore = Ext.create("Ext.data.Store", {
    fields: ["Name", "Value"],
    data: [
        { text: "通过", Value: "通过" },
        { text: "退回", Value: "退回"}
    ]
});
Ext.define('Deputycurator.view.ApproveAddView', {
    extend: 'Ext.window.Window',
    xtype: 'approveAddView',
    itemId:'approveAddViewId',
    title: '副馆长审阅',
    frame: true,
    resizable: true,
    autoScroll: true,
    width: '80%',
    height: '80%',
    modal:true,
    closeToolText:'关闭',
    layout: {
        type: 'vbox',
        align: 'stretch'
    },

    defaults: {
        layout: 'form',
        xtype: 'container',
        defaultType: 'textfield',
        style: 'width: 50%'
    },
    items: [{
        xtype: 'form',
        modelValidation: true,
        itemId:'selectApprove',
        items: [{
            fieldLabel: "批示",
            columnWidth: 1,
            itemId: 'approveId',
            xtype: 'textarea',
            labelWidth: 85,
            name: 'approve',
            readOnly: true
        }, {xtype: "combobox",
                fieldLabel: "审核结果",
                itemId:'selectApproveId',
                margin:'15',
                store: passStore,
                editable: false,
                allowBlank: false,
                displayField: "text",
                valueField: "Value",
                emptyText: "--请选择--",
                queryMode: "local"},
            {
                columnWidth: 1,
                itemId:'addproveId',
                xtype: 'textarea',
                fieldLabel: '添加批示',
                name:'addprove',
                margin:'15',
                flex: 1,
                margin: '5 0 0 0'
            },{
                columnWidth: .47,
                xtype: 'combo',
                store: 'NextNodeStore',
                itemId: 'nextNodeId',
                fieldLabel: '下一环节',
                labelWidth: 85,
                displayField: 'text',
                allowBlank: false,
                queryMode: 'local',
                editable: false,
                valueField: 'id',
                margin: '10 0 0 0',
                afterLabelTextTpl: [
                    '<span style="color:red;font-weight:bold" data-qtip="必填项">*</span>'],
                listeners: {
                    beforerender: function (combo) {
                        combo.getStore().on('load',function () {
                            var store = combo.getStore();
                            if(store.getCount()>0){
                                var record = store.getAt(0);
                                combo.select(record);
                                combo.fireEvent("select",combo,record);
                            }
                        });
                    },
                    select: function (combo, record) {
                        var approveAddView = combo.findParentByType("approveAddView");
                        var spmanOrgan = approveAddView.down("[itemId=approveOrgan]");
                        spmanOrgan.select(null);
                        var nextSpman = approveAddView.down("[itemId=nextSpmanId]");
                        nextSpman.select(null);
                        nextSpman.getStore().removeAll();
                        nextSpman.getStore().proxy.extraParams.nodeId = record.get('id');
                        spmanOrgan.getStore().proxy.extraParams.type = "submit"; //审批时获取审批单位
                        spmanOrgan.getStore().proxy.extraParams.taskid = null;
                        spmanOrgan.getStore().proxy.extraParams.nodeid = record.get('id');
                        spmanOrgan.getStore().proxy.extraParams.worktext = null;
                        spmanOrgan.getStore().proxy.extraParams.approveType = "project"; //审批类型
                        spmanOrgan.getStore().reload(); //刷新审批单位
                    }
                }
            },{
                xtype : 'combo',
                columnWidth: .23,
                store : 'ApproveOrganStore',
                itemId:'approveOrgan',
                fieldLabel: '审批单位',
                displayField : 'organname',
                queryMode:'local',
                editable:false,
                valueField : 'organid',
                margin: '10 0 0 0',
                listeners:{
                    beforerender: function (combo) {
                        combo.getStore().on('load',function () {
                            var store = combo.getStore();
                            if(store.getCount()>0){
                                var record = store.getAt(0);
                                combo.select(record);
                                combo.fireEvent("select",combo,record);
                            }
                        });
                    },
                    select:function (combo,record) {
                        var spmancombo = combo.findParentByType("approveAddView").down("[itemId=nextSpmanId]");
                        spmancombo.select(null);
                        var spmanStore = spmancombo.getStore();
                        spmanStore.proxy.extraParams.organid = record.get("organid");
                        spmanStore.reload();
                    },
                    render: function(sender) {
                        new Ext.ToolTip({
                            target: sender.el,
                            trackMouse: true,
                            dismissDelay: 0,
                            anchor: 'buttom',
                            html: ' <i class="fa fa-info-circle"></i> '+"支持跨单位查档申请，请选择要查档的单位！"
                        });
                    }
                }
            },{
                columnWidth: .23,
                xtype: 'combo',
                store: 'NextSpmanStore',
                itemId: 'nextSpmanId',
                name: 'spman',
                queryMode: 'local',
                fieldLabel: '审批人',
                labelWidth: 85,
                displayField: 'realname',
                editable: false,
                valueField: 'userid',
                margin: '10 0 0 0',
                afterLabelTextTpl: [
                    '<span style="color:red;font-weight:bold" data-qtip="必填项">*</span>'],
                listeners: {
                    beforerender: function (combo) {
                        combo.getStore().on('load', function () {
                            var store = combo.getStore();
                            if (store.getCount() > 0) {
                                var record = store.getAt(0);
                                combo.select(record);
                                combo.fireEvent("select", combo, record);
                            }
                        });
                    }
                }
            }
        ]
    }],

    buttons: [
        { text: '完成',itemId:'approveAddSubmit'},
        { text: '关闭',itemId:'approveAddClose'}
    ]
});