/**
 * Created by yl on 2017/10/31.
 */
Ext.define('Appraisal.view.AppraisalDestroyView', {
    extend: 'Ext.window.Window',
    xtype: 'appraisalDestroyView',
    title: '新增销毁单据',
    width: 700,
    height: 320,
    minWidth: 700,
    minHeight: 320,
    layout: 'fit',
    autoShow: true,
    modal: true,
    resizable: false,//是否可以改变窗口大小
    closeToolText: '关闭',
    items: [
        {
            xtype: 'panel',
            layout: 'card',
            activeItem: 0,
            items: [
                {
                    xtype: 'form',
                    itemId: 'firstCard',
                    autoScroll: true,
                    layout: 'column',
                    fieldDefaults: {
                        labelWidth: 80
                    },
                    bodyPadding: 15,
                    items: [
                        {
                            columnWidth: 1,
                            xtype: 'textfield',
                            fieldLabel: '单据题名<span style="color: #CC3300; padding-right: 2px;">*</span>',
                            allowBlank: false,
                            itemId: 'titleID',
                            name: 'title',
                            blankText: '该输入项为必输项',
                            margin: '5 0 5 0'
                        },
                        {
                            columnWidth: 0.5,
                            xtype: 'datefield',
                            fieldLabel: '单据时间<span style="color: #CC3300; padding-right: 2px;">*</span>',
                            allowBlank: false,
                            itemId: 'approvaldateID',
                            name: 'approvaldate',
                            blankText: '该输入项为必输项',
                            format: 'Y-m-d H:i:s',
                            value: new Date(),
                            margin: '5 10 5 0'
                        },
                        {
                            columnWidth: 0.5,
                            xtype: 'textfield',
                            fieldLabel: '条目总数',
                            margin: '5 0 5 0',
                            name: 'total',
                            readOnly: true
                        },
                        {
                            columnWidth: 1,
                            xtype: 'textarea',
                            fieldLabel: '销毁原因',
                            height: 100,
                            margin: '5 0 5 0',
                            name: 'reason'
                        }, {
                            xtype: 'hidden',
                            name: 'nodeid'
                        }]
                }, {
                    xtype: 'form',
                    itemId: 'secondCard',
                    layout: 'column',
                    fieldDefaults: {
                        labelWidth: 80
                    },
                    bodyPadding: 15,
                    items: [{
                        xtype: 'combo',
                        itemId: 'nextnode',
                        store: 'ApproveNodeStore',
                        fieldLabel: '下一环节',
                        displayField: 'text',
                        valueField: 'id',
                        allowBlank: false,
                        editable: false,
                        columnWidth: .47,
                        name: 'nextNode',
                        queryMode: "local",
                        margin: '5 0 5 0',
                        listeners: {
                            beforerender: function (combo) {
                                var store = combo.getStore();
                                store.on("load",function () {
                                    if(store.getCount()>0){
                                        var record = store.getAt(0);
                                        combo.select(record);
                                        combo.fireEvent("select",combo,record);
                                    }
                                });
                            },
                            select:function (combo,record) {
                                var spmanOrgan = combo.findParentByType("appraisalDestroyView").down("[itemId=approveOrgan]");
                                spmanOrgan.select(null);
                                var spman = combo.findParentByType("appraisalDestroyView").down("[itemId=spmanId]");
                                spman.select(null);
                                spman.getStore().removeAll();
                                spman.getStore().proxy.extraParams.nodeId = record.get('id');
                                spmanOrgan.getStore().proxy.extraParams.type = "submit"; //审批时获取审批单位
                                spmanOrgan.getStore().proxy.extraParams.taskid = null;
                                spmanOrgan.getStore().proxy.extraParams.nodeid = record.get('id');
                                spmanOrgan.getStore().proxy.extraParams.worktext = null;
                                spmanOrgan.getStore().proxy.extraParams.approveType = "bill"; //审批类型
                                spmanOrgan.getStore().reload(); //刷新审批单位
                            }
                        }
                    }, {
                        columnWidth:.06,
                        xtype:'displayfield'
                    }, {
                        columnWidth: .47,
                        xtype: 'combobox',
                        itemId:'approveOrgan',
                        store: 'ApproveOrganStore',
                        fieldLabel: '审批单位',
                        queryMode: "local",
                        allowBlank: false,
                        displayField: 'organname',
                        valueField: 'organid',
                        editable: false,
                        margin: '5 0 5 0',
                        listeners: {
                            beforerender: function (combo) {
                                var store = combo.getStore();
                                store.on("load",function () {
                                    if(store.getCount()>0){
                                        var record = store.getAt(0);
                                        combo.select(record);
                                        combo.fireEvent("select",combo,record);
                                    }
                                });
                            },
                            select:function (combo,record) {
                                var spmancombo = combo.findParentByType("appraisalDestroyView").down("[itemId=spmanId]");
                                spmancombo.select(null);
                                var spmanStore = spmancombo.getStore();
                                spmanStore.proxy.extraParams.findOrganid = record.get("organid");
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
                        xtype: 'combo',
                        itemId: 'spmanId',
                        store: 'ApproveManStore',
                        fieldLabel: '审批人',
                        columnWidth: appraisalSendmsg==true?.7:1,
                        displayField: 'realname',
                        valueField: 'userid',
                        queryMode: "local",
                        editable: false,
                        margin: '5 0 5 0',
                        listeners: {
                            beforerender: function (combo) {
                                var store = combo.getStore();
                                store.on("load",function () {
                                    if(store.getCount()>0){
                                        var record = store.getAt(0);
                                        combo.select(record);
                                        combo.fireEvent("select",combo,record);
                                    }
                                });
                            }
                        }
                    },{
                        columnWidth:.1,
                        xtype:'displayfield',
                        hidden:appraisalSendmsg==true?false:true
                    },{
                        columnWidth: .2,
                        xtype: 'checkbox',
                        itemId:'sendmsgId',
                        labelWidth:85,
                        inputValue : true,
                        margin: '10 0 0 0',
                        fieldLabel:'发送短信',
                        hidden:appraisalSendmsg==true?false:true
                    },{
                        columnWidth: 1,
                        xtype: 'label',
                        text: '点击确定后，直接生成单据进入审批环节，可在销毁单据管理-待审核中进行查看',
                        style:{
                            color:'red',
                            'font-size':'15px',
                            'font-weight':'bold'
                        },
                        margin: '5 0 5 110'
                    }]
                }
            ]
        }
    ],
    buttons: [{
        xtype: "label",
        itemId:'tips',
        style:{color:'red'},
        text:'温馨提示：红色外框表示输入非法数据！',
        margin:'6 2 5 4'
    }, {
        itemId: 'approval',
        text: '送审'
    }, {
        itemId: 'save',
        text: '保存'
    }, {
        itemId: 'confirm',
        text: '确定'
    }, {
        itemId: 'back',
        text: '返回'
    }, {
        itemId: 'close',
        text: '关闭'
    }]
});