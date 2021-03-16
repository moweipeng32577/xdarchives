/**
 * Created by Administrator on 2019/10/25.
 */
Ext.define('TransforAuditDeal.view.TranforAuditDealView', {
    extend: 'Ext.panel.Panel',
    xtype: 'tranforAuditDealView',
    layout: 'card',
    activeItem: 0,
    tbar: [{
        text:'查看',
        iconCls:'fa fa-eye',
        itemId:'lookDoc'
    },'-',{
        xtype : 'combo',
        store : 'NextNodeStore',
        itemId:'nextNodeId',
        fieldLabel: '下一环节',
        labelWidth: 60,
        displayField : 'text',
        editable:false,
        valueField : 'id',
        queryMode:'local',
        hidden:type!='完成'?false:true,
        listeners:{
            afterrender:function(combo){
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
                if (type != '完成') {
                    var spmanOrgan = combo.findParentByType("tranforAuditDealView").down("[itemId=approveOrgan]");
                    spmanOrgan.select(null);
                    var nextSpman = combo.findParentByType("tranforAuditDealView").down("[itemId=nextSpmanId]");
                    nextSpman.select(null);
                    nextSpman.getStore().removeAll();
                    nextSpman.getStore().proxy.extraParams.nodeId = record.get('id');
                    spmanOrgan.getStore().proxy.extraParams.type = "approve"; //审批时获取审批单位
                    spmanOrgan.getStore().proxy.extraParams.taskid = taskid;
                    spmanOrgan.getStore().proxy.extraParams.nodeid = record.get('id');
                    spmanOrgan.getStore().proxy.extraParams.worktext = null;
                    spmanOrgan.getStore().proxy.extraParams.approveType = "audit"; //审批类型
                    spmanOrgan.getStore().load(); //刷新审批单位
                }
            }
        }
    },'-',{
        xtype : 'combo',
        store : 'ApproveOrganStore',
        itemId:'approveOrgan',
        fieldLabel: '审批单位',
        labelWidth: 60,
        displayField : 'organname',
        editable:false,
        valueField : 'organid',
        queryMode:'local',
        hidden:type!='完成'?false:true,
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
                if (type != '完成') {
                    var spmancombo = combo.findParentByType("tranforAuditDealView").down("[itemId=nextSpmanId]");
                    spmancombo.select(null);
                    var spmanStore = spmancombo.getStore();
                    spmanStore.proxy.extraParams.organid = record.get("organid");
                    spmanStore.reload();
                }
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
    },'-',{
        xtype : 'combo',
        store : 'NextSpmanStore',
        itemId:'nextSpmanId',
        name:'spman',
        fieldLabel: '审批人',
        displayField : 'realname',
        queryMode:'local',
        labelWidth: 50,
        editable:false,
        valueField : 'userid',
        hidden:type!='完成'?false:true,
        listeners:{
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
    },'-',{
        text:'完成',
        iconCls:'fa fa-table',
        itemId:'move',
        hidden:type!='完成'?false:true
    },'-',{
        text:'退回',
        iconCls:'fa fa-reply-all',
        itemId:'sendback',
        hidden:type!='完成'?false:true
    },'-',{
        text:'返回',
        iconCls:'fa fa-reply-all',
        itemId:'back',
    }],
    items: [{
        layout:'border',
        itemId:'gridview',
        items: [{
            //     itemId: 'auditDocId',
            //     region: 'center',
            //     xtype: 'auditDocGridView'
            // }, {
                region: 'center',
                xtype: 'auditgrid',
                itemId: 'onlygrid'
            },{
            region: 'south',
            height: '50%',
            xtype: 'panel',
            title: '卷内条目',
            collapsible: true,
            collapseToolText: '收起',
            expandToolText: '展开',
            collapsed: false,
            split: true,
            allowDrag: true,
            expandOrcollapse: 'expand',//默认打开
            layout: 'fit',
            items: [{
                xtype: 'auditVolumeGrid',
                itemId: 'auditVolumeGrid'
            }]
        }]
    }, {
        xtype: 'AuditFormView'
    }]
});
