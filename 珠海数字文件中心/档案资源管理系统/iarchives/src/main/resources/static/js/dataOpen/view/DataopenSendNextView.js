/**
 * Created by tanly on 2017/12/4 0004.
 */
Ext.define('Dataopen.view.DataopenSendNextView', {
    extend: 'Ext.form.Panel',
    xtype: 'dataopenSendNextView',
    itemId: 'dataopenSendNextViewId',
    region: 'center',
    autoScroll: true,
    fieldDefaults: {
        labelWidth: 110
    },
    layout: 'column',
    bodyPadding: '10 30 10 30',
    items: [{xtype: 'textfield', name: 'id', hidden: true},
        {
            columnWidth: 1,
            xtype: 'textfield',
            fieldLabel: '工作流名称',
            name: 'id',
            itemId: 'flowItem',
            margin: '10 0 0 0',
            value: '开放审批',
            editable: false
        }, {
            columnWidth: 1,
            xtype: 'textfield',
            fieldLabel: '任务名称',
            name: 'taskname',
            itemId: 'taskItem',
            margin: '10 0 0 0'
        }, {
            columnWidth:.47,
            xtype: 'combo',
            store: 'DataopenNodeStore',
            name: 'nodename',
            itemId: 'nodenameItem',
            fieldLabel: '环节名称',
            displayField: 'text',
            valueField: 'id',
            queryMode: "local",
            editable: false,
            margin: '10 0 0 0',
            listeners: {
                afterrender: function (combo) {
                    var store = combo.getStore();
                    store.load(function () {
                        if (this.getCount() > 0) {
                            combo.select(this.getAt(0));
                        }
                    });
                }
            }
        }, {
            columnWidth: .06,
            xtype: 'displayfield'
        },{
            columnWidth: .47,
            xtype: 'combobox',
            itemId:'approveOrgan',
            store: 'ApproveOrganStore',
            fieldLabel: '审批单位',
            queryMode: "local",
            labelWidth: 85,
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
                    var spmancombo = combo.findParentByType("dataopenSendNextView").down("[itemId=nodeuserItem]");
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
            columnWidth: openSendmsg==true?.7:1,
            xtype: 'combo',
            store: 'DataopenNodeuserStore',
            name: 'nodeuser',
            itemId: 'nodeuserItem',
            fieldLabel: '环节用户',
            displayField: 'realname',
            valueField: 'userid',
            queryMode: "local",
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
        },{
            columnWidth:.1,
            xtype:'displayfield',
            hidden:openSendmsg==true?false:true
        },{
            columnWidth: .2,
            xtype: 'checkbox',
            itemId:'sendmsgId',
            labelWidth:85,
            inputValue : true,
            margin: '10 0 0 0',
            fieldLabel:'发送短信',
            hidden:openSendmsg==true?false:true
        }
    ],
    buttons: [
        {text: '提交', itemId: 'submit'},
        {text: '返回', itemId: 'back'}
    ]
});