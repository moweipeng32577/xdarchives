/**
 * Created by yl on 2017/12/4.
 */
Ext.define('DestructionBill.view.DestructionApprovalView', {
    extend: 'Ext.window.Window',
    xtype: 'destructionApprovalView',
    title: '送审',
    width: 300,
    height: "30%",
    modal: true,
    resizable: false,
    closeToolText:'关闭',
    layout: 'fit',
    items: [
        {
            xtype: 'form',
            fileUpload: true,
            layout: {
                type: 'vbox',
                align: 'middle'
            },
            bodyPadding: 15,
            items: [{
                xtype: 'textfield',
                itemId: 'nextnode',
                fieldLabel: '下一环节',
                editable: false,
                name:'nextNode',
                labelWidth: 80
            },{
                xtype: 'combobox',
                itemId:'approveOrgan',
                fieldLabel: '审批单位',
                editable: false,
                store: 'ApproveOrganStore',
                queryMode: "local",
                allowBlank: false,
                displayField: 'organname',
                valueField: 'organid',
                labelWidth: 80,
                listeners: {
                    afterrender: function (combo) {
                        var store = combo.getStore();
                        store.on("load",function () {
                            if(store.getCount()>0){
                                var record = store.getAt(0);
                                combo.select(record);
                                // combo.fireEvent("select",combo,record);
                            }
                        });
                    },
                    select:function (combo,record) {
                        var spmancombo = combo.findParentByType("destructionApprovalView").down("[itemId=spmanId]");
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
                displayField: 'realname',
                valueField: 'userid',
                editable: false,
                labelWidth: 80,
                listeners: {
                    afterrender: function (combo) {
                        var store = combo.getStore();
                        if (store.getCount() > 0) {
                            combo.select(store.getAt(0));
                        }
                    }
                }
            }],
            buttons: [{
                itemId: 'submit',
                text: '保存'
            }, {
                itemId: 'back',
                text: '返回'
            }]
        }
    ]
});