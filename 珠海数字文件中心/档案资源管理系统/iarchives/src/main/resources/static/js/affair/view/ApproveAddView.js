/**
 * Created by Administrator on 2017/10/23 0023.
 */
Ext.define('Affair.view.ApproveAddView', {
    extend: 'Ext.window.Window',
    xtype: 'approveAddView',
    itemId:'approveAddViewId',
    title: '综合事务部整理',
    frame: true,
    resizable: true,
    autoScroll: true,
    width: '20%',
    height: '30%',
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
        items: [
            {
                columnWidth: .47,
                xtype: 'combo',
                itemId: 'auditlinkId',
                store: 'AffairNodeStore',
                queryMode:'local',
                fieldLabel: '审核环节',
                labelWidth: 85,
                displayField: 'text',
                valueField: 'id',
                queryMode: "local",
                allowBlank: false,
                editable: false,
                margin: '10 0 0 0',
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
                        var spmanOrgan = combo.findParentByType("approveAddView").down("[itemId=approveOrgan]");
                        spmanOrgan.select(null);
                        var spman = combo.findParentByType("approveAddView").down("[itemId=spmanId]");
                        spman.select(null);
                        spman.getStore().removeAll();
                        spman.getStore().proxy.extraParams.nodeId = record.get('id');
                        spmanOrgan.getStore().proxy.extraParams.type = "submit"; //审批时获取审批单位
                        spmanOrgan.getStore().proxy.extraParams.taskid = null;
                        spmanOrgan.getStore().proxy.extraParams.nodeid = record.get('id');
                        spmanOrgan.getStore().proxy.extraParams.worktext = null;
                        spmanOrgan.getStore().proxy.extraParams.approveType = "affair"; //审批类型
                        spmanOrgan.getStore().reload(); //刷新审批单位
                    }
                }
            },
            {
                columnWidth: .23,
                xtype: 'combobox',
                itemId:'approveOrgan',
                store: 'ApproveOrganStore',
                fieldLabel: '审批单位',
                labelWidth: 85,
                queryMode: "local",
                allowBlank: false,
                displayField: 'organname',
                valueField: 'organid',
                editable: false,
                margin: '10 0 0 0',
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
                    select:function (combo,record) {
                        var spmancombo = combo.findParentByType("approveAddView").down("[itemId=spmanId]");
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
                columnWidth: .23,
                xtype: 'combo',
                itemId: 'spmanId',
                store: 'ApproveManStore',
                queryMode:'local',
                fieldLabel: '审核人',
                labelWidth: 85,
                displayField: 'realname',
                valueField: 'userid',
                queryMode: "local",
                allowBlank: false,
                editable: false,
                margin: '10 0 0 0',
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
            }
        ]
    }],

    buttons: [
        { text: '提交',itemId:'approveAddSubmit'},
        { text: '关闭',itemId:'approveAddClose'}
    ]
});