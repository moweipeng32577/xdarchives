/**
 * Created by Administrator on 2020/6/15.
 */

var passStore = Ext.create("Ext.data.Store", {
    fields: ["Name", "Value"],
    data: [
        { text: "同意", Value: "同意" },
        { text: "不同意", Value: "不同意"}
    ]
});

Ext.define('AuditOrder.view.CarOrderAuditFormView', {
    extend: 'Ext.panel.Panel',
    xtype: 'carOrderAuditFormView',
    itemId: 'carOrderAuditFormViewId',
    autoScroll: true,
    layout: 'fit',
    items: [{
        xtype: 'form',
        layout: 'column',
        autoScroll: true,
        bodyPadding: 16,
        fieldDefaults: {
            labelWidth: 70
        },
        items: [{
            xtype: 'textfield',
            name: 'id',
            hidden: true
        }, {
            columnWidth: .47,
            fieldLabel: '用车人',
            itemId: 'caruserId',
            xtype: 'textfield',
            name: 'caruser',
            labelWidth: 85,
            margin: '20 0 0 0'
        }, {
            columnWidth: .06,
            xtype: 'displayfield'
        }, {
            columnWidth: .47,
            fieldLabel: '单位',
            xtype: 'textfield',
            name: 'userorgan',
            labelWidth: 85,
            margin: '20 0 0 0'
        }, {
            columnWidth: .47,
            fieldLabel: '联系电话',
            xtype: 'textfield',
            name: 'phonenumber',
            labelWidth: 85,
            margin: '20 0 0 0'
        }, {
            columnWidth: .06,
            xtype: 'displayfield'
        }, {
            columnWidth: .47,
            xtype: 'textfield',
            itemId: 'usewayId',
            name: 'useway',
            fieldLabel: '使用用途',
            labelWidth: 85,
            margin: '20 0 0 0'
        }, {
            columnWidth: .47,
            fieldLabel: '预约时间',
            name: 'ordertime',
            xtype: 'textfield',
            labelWidth: 85,
            editable: false,
            margin: '20 0 0 0',
            afterLabelTextTpl: [
                '<span style="color:red;font-weight:bold" data-qtip="必填项">*</span>']
        }, {
            columnWidth: .06,
            xtype: 'displayfield'
        }, {
            columnWidth: .23,
            fieldLabel: '开始时间',
            xtype: 'datetimefield',
            name: 'starttime',
            format: 'Y-m-d H:i',
            labelWidth: 85,
            editable: false,
            margin: '20 0 0 0'
        }, {
            columnWidth: .01,
            xtype: 'displayfield'
        }, {
            columnWidth: .23,
            fieldLabel: '结束时间',
            xtype: 'datetimefield',
            format: 'Y-m-d H:i',
            name: 'endtime',
            labelWidth: 85,
            editable: false,
            margin: '20 0 0 0'
        }, {
            columnWidth: 1,
            itemId: 'remarkid',
            xtype: 'textarea',
            fieldLabel: '备注',
            labelWidth: 85,
            name: 'remark',
            flex: 1,
            margin: '20 0 0 0',
            readOnly: true
        }, {
            columnWidth: 1,
            itemId: 'approveId',
            xtype: 'textarea',
            fieldLabel: '批注',
            labelWidth: 85,
            name: 'approve',
            flex: 1,
            margin: '20 0 0 0',
            readOnly: true
        },{
            columnWidth: 1,
            xtype:'fieldset',
            height:200,
            margin:'30 0 0 0',
            title: '审批信息',
            layout:'column',
            items: [
                {
                    columnWidth: .2,
                    xtype: "combo",
                    fieldLabel: "审批意见",
                    itemId: 'selectApproveId',
                    labelWidth: 85,
                    store: passStore,
                    editable: false,
                    allowBlank: false,
                    displayField: "text",
                    valueField: "Value",
                    queryMode: "local",
                    margin: '10 0 0 0',
                    afterLabelTextTpl: [
                        '<span style="color:red;font-weight:bold" data-qtip="必填项">*</span>'],
                    listeners: {
                        afterrender: function (combo) {
                            var store = combo.getStore();
                            if (store.getCount() > 0) {
                                setTimeout(function () {
                                    var record = store.getAt(0);
                                    combo.select(record);
                                    combo.fireEvent("select", combo, record);
                                },300);
                            }
                        },
                        select:function (view,record) {
                            var carOrderAuditFormView = view.findParentByType('carOrderAuditFormView');
                            var addprove = carOrderAuditFormView.down('[itemId=addproveId]');
                            addprove.setValue(record.get('Value'));
                        }
                    }
                }, {
                    columnWidth: .06,
                    xtype: 'displayfield'
                }, {
                    columnWidth: .2,
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
                            var carOrderAuditFormView = combo.findParentByType("carOrderAuditFormView");
                            var spmanOrgan = carOrderAuditFormView.down("[itemId=approveOrgan]");
                            spmanOrgan.select(null);
                            var nextSpman = carOrderAuditFormView.down("[itemId=nextSpmanId]");
                            nextSpman.select(null);
                            nextSpman.getStore().removeAll();
                            nextSpman.getStore().proxy.extraParams.nodeId = record.get('id');
                            spmanOrgan.getStore().proxy.extraParams.type = "approve"; //审批时获取审批单位
                            spmanOrgan.getStore().proxy.extraParams.taskid = carOrderAuditFormView.orderid;
                            spmanOrgan.getStore().proxy.extraParams.nodeid = record.get('id');
                            spmanOrgan.getStore().proxy.extraParams.worktext = null;
                            spmanOrgan.getStore().proxy.extraParams.approveType = "carOrder"; //审批类型
                            spmanOrgan.getStore().reload(); //刷新审批单位
                        }
                    }
                } ,{
                    columnWidth: .06,
                    xtype: 'displayfield'
                },{
                    xtype : 'combo',
                    columnWidth: .2,
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
                            var spmancombo = combo.findParentByType("carOrderAuditFormView").down("[itemId=nextSpmanId]");
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
                }, {
                    columnWidth: .06,
                    xtype: 'displayfield'
                },{
                    columnWidth: .2,
                    xtype: 'combo',
                    store: 'ApproveManStore',
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
                },{
                    columnWidth: 1,
                    xtype: 'textarea',
                    itemId: 'addproveId',
                    name: 'addprove',
                    fieldLabel: '添加批注',
                    margin: '20 0 0 0',
                    labelWidth: 85
                }
            ]
        }]
    }],
    buttons: [{
        text: '提交',
        itemId: 'carOrderApproveSubmit'
    }, {
        text: '关闭',
        itemId: 'carOrderApproveClose'
    }]
});
