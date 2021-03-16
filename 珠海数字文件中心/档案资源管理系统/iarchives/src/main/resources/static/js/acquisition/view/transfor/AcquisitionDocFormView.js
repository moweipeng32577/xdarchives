/**
 * Created by tanly on 2017/12/11 0011.
 */
Ext.define('Acquisition.view.transfor.AcquisitionDocFormView', {
    extend: 'Ext.window.Window',
    xtype: 'acquisitionDocFormView',
    itemId: 'acquisitionDocFormViewid',
    title: '移交',
    width: 780,
    height: 450,
    modal: true,
    closeToolText:'关闭',
    layout: 'fit',
    items: [{
        layout: {
            type: 'vbox',
            align: 'stretch'
        },
        xtype: 'form',
        itemId: 'formitemid',
        margin: '22',
        items: [{
            xtype:'textfield',
            fieldLabel: '',
            name: 'docid',
            hidden: true
            // itemId: 'nodeiditemid'
        }, {
            xtype: 'textfield',
            fieldLabel: '交接工作名称',
            name: 'transfertitle'
        },{
            xtype:'textarea',
            fieldLabel: '内容描述',
            name: 'transdesc',
            itemId: 'refiditemid'
        }, {
            layout: 'column',
            itemId:'multcolumnId',
            items: [{
                columnWidth: 1,
                items: [{
                    xtype: 'textfield',
                    fieldLabel: '移交人',
                    name: 'transuser',
                    // itemId: 'nodenameitemid',
                    style: 'width: 100%'
                }]
            },{
                columnWidth: .47,
                items: [{
                    xtype: 'textfield',
                    fieldLabel: '载体起止顺序号',
                    name: 'sequencecode',
                    style: 'width: 100%'
                }]
            },{
                columnWidth:.06,
                xtype:'displayfield'
            },{
                columnWidth: .47,
                items: [{
                    xtype: 'textfield',
                    fieldLabel: '移交电子档案数',
                    name: 'transcount',
                    style: 'width: 100%'
                }]
            },{
                columnWidth: .47,
                items: [{
                    xtype: 'textfield',
                    fieldLabel: '移交数据量(M)',
                    name: 'transfersize',
                    style: 'width: 100%'
                }]
            },{
                columnWidth:.06,
                xtype:'displayfield'
            },{
                columnWidth: .47,
                items: [{
                    xtype: 'textfield',
                    fieldLabel: '移交部门',
                    editable: false,
                    name: 'transorgan',
                    // itemId: 'nodenameitemid',
                    style: 'width: 100%'
                }]
            },{
                columnWidth: .47,
                xtype: 'combo',
                itemId: 'approveNodeId',
                store: 'ApproveNodeStore',
                queryMode:'local',
                fieldLabel: '审批环节',
                displayField: 'text',
                valueField: 'id',
                editable: false,
                hidden:auditOpened == 'true' ? false:true,
                listeners: {
                    afterrender: function (combo) {
                        var store = combo.getStore();
                        if (store.getCount() > 0) {
                            combo.select(store.getAt(0));
                        }
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
                        var spmanOrgan = combo.findParentByType("acquisitionDocFormView").down("[itemId=approveOrgan]");
                        //spmanOrgan.select(null);
                        var spman = combo.findParentByType("acquisitionDocFormView").down("[itemId=spmanId]");
                        //spman.select(null);
                        //spman.getStore().removeAll();
                        spman.getStore().proxy.extraParams.nodeId = record.get('id');
                        spmanOrgan.getStore().proxy.extraParams.type = "submit"; //审批时获取审批单位
                        spmanOrgan.getStore().proxy.extraParams.taskid = null;
                        spmanOrgan.getStore().proxy.extraParams.nodeid = record.get('id');
                        spmanOrgan.getStore().proxy.extraParams.worktext = "采集移交审核";;
                        spmanOrgan.getStore().proxy.extraParams.approveType = "audit"; //审批类型
                        spmanOrgan.getStore().reload(); //刷新审批单位
                    }
                }
            },{
                columnWidth:.06,
                xtype:'displayfield',
                hidden:auditOpened == 'true' ? false:true
            },{
                columnWidth: .47,
                items: [{
                    fieldLabel: '实体移交时间',
                    xtype: 'textfield',
                    name: 'transdate',
                    format: 'Y-m-d H:i:s',
                    style: 'width: 100%',
                    editable: false,
                    value:new Date().format('yyyy-MM-dd hh:mm:ss')
                }]
            },{
                columnWidth: .47,
                xtype: 'combo',
                itemId:'approveOrgan',
                store: 'ApproveOrganStore',
                fieldLabel: '审批单位',
                queryMode: "local",
                allowBlank: false,
                displayField: 'organname',
                valueField: 'organid',
                editable: false,
                hidden:auditOpened == 'true' ? false:true,
                listeners: {
                    afterrender: function (combo,record) {
                        var store = combo.getStore();
                        if (store.getCount() > 0) {
                            combo.select(store.getAt(0));
                        }
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
                        var spmancombo = combo.findParentByType("acquisitionDocFormView").down("[itemId=spmanId]");
                        //spmancombo.select(null);
                        var spmanStore = spmancombo.getStore();
                        spmanStore.proxy.extraParams.organid = record.get("organid");
                        spmanStore.proxy.extraParams.worktext = "采集移交审核";
                        spmanStore.reload();
                    }/*,
                    render: function(sender) {
                        new Ext.ToolTip({
                            target: sender.el,
                            trackMouse: true,
                            dismissDelay: 0,
                            anchor: 'buttom',
                            html: ' <i class="fa fa-info-circle"></i> '+"支持跨单位查档申请，请选择要查档的单位！"
                        });
                    }*/
                }
            }, {
                columnWidth:.06,
                xtype:'displayfield'
            },{
                columnWidth: .47,
                xtype: 'combo',
                itemId: 'spmanId',
                store: 'ApproveManStore',
                queryMode:'local',
                fieldLabel: '审批人',
                displayField: 'realname',
                valueField: 'userid',
                allowBlank: false,
                editable: false,
                hidden:auditOpened == 'true' ? false:true,
                listeners: {
                    afterrender: function (combo) {
                        var store = combo.getStore();
                        if (store.getCount() > 0) {
                            combo.select(store.getAt(0));
                        }
                        combo.getStore().on('load',function () {
                            var store = combo.getStore();
                            if(store.getCount()>0){
                                var record = store.getAt(0);
                                combo.select(record);
                                combo.fireEvent("select",combo,record);
                            }
                        });
                    }
                }
            // },{
            //     itemId:'isSynch',
            //     columnWidth:0.5,
            //     xtype:'checkbox',
            //     boxLabel:'同步移交卷内',
            //     checked:false
            }]
        }
        ]
    }]
    ,
    buttons: [{
        text: '移交',
        itemId: 'SendBtnID'
    }, {
        text: '取消',
        itemId: 'CancelBtnID'
    }
    ]
});