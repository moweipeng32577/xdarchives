/**
 * Created by RonJiang on 2017/10/31 0031.
 */

Ext.define('BillApproval.view.BillApprovalFormView',{
    extend: 'Ext.form.Panel',
    xtype: 'billApprovalFormView',
    itemId:'billApprovalFormViewId',
    region: 'center',
    title:'审核单据',
    autoScroll: true,
    fieldDefaults: {
        labelWidth: 60
    },
    layout:'column',
    bodyPadding: 15,
    items:[
        { xtype: 'textfield',name:'id',hidden:true},
        {
        columnWidth: .47,
        xtype: 'textfield',
        fieldLabel: '送审人',
        name:'submitusername',
        margin:'10 0 0 0',
        readOnly:true
    },{
        columnWidth:.06,
        xtype:'displayfield'
    },{
        columnWidth: .47,
        xtype: 'textfield',
        fieldLabel: '送审时间',
        name:'submitdate',
        margin:'10 0 0 0',
        readOnly:true
     },{
        columnWidth: 1,
        itemId:'approveId',
        xtype: 'textarea',
        fieldLabel: '批示',
        name:'approve',
        flex: 1,
        margin: '5 0 0 0',
        readOnly:true
       // disabled:true
    }]
    ,buttons:[{
        xtype: 'checkbox',
        itemId:'sendmsgId',
        inputValue : true,
        fieldLabel:'发送短信',
        margin: '0 30 0 0',
        hidden:true
    }, { text: '添加批示',
         itemId:'approveAdd'
    }, {
            xtype : 'combo',
            store : 'NextNodeStore',
            itemId:'nextNodeId',
            name:'spman',
            fieldLabel: '下一环节',
            displayField : 'text',
            editable:false,
            valueField : 'id',
            style: "margin-left:24px",
            listeners:{
                afterrender:function(combo){
                    var store = combo.getStore();
                    store.load(function(data){
                        if(this.getCount() > 0){
                            combo.select(this.getAt(0));
                        }
                    });
                },
                select:function (combo,record) {
                    var spmanOrgan = combo.findParentByType("billApprovalFormView").down("[itemId=approveOrgan]");
                    spmanOrgan.select(null);
                    var nextSpman = combo.findParentByType("billApprovalFormView").down("[itemId=nextSpmanId]");
                    nextSpman.select(null);
                    nextSpman.getStore().removeAll();
                    nextSpman.getStore().proxy.extraParams.nodeId = record.get('id');
                    spmanOrgan.getStore().proxy.extraParams.type = "approve"; //审批时获取审批单位
                    spmanOrgan.getStore().proxy.extraParams.taskid = taskid;
                    spmanOrgan.getStore().proxy.extraParams.nodeid = record.get('id');
                    spmanOrgan.getStore().proxy.extraParams.worktext = null;
                    spmanOrgan.getStore().proxy.extraParams.approveType = "bill"; //审批类型
                    spmanOrgan.getStore().reload(); //刷新审批单位
                }
            }
        },{
        xtype : 'combo',
        store : 'ApproveOrganStore',
        itemId:'approveOrgan',
        fieldLabel: '审批单位',
        displayField : 'organname',
        queryMode:'local',
        editable:false,
        valueField : 'organid',
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
                var spmancombo = combo.findParentByType("billApprovalFormView").down("[itemId=nextSpmanId]");
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
            xtype : 'combo',
            store : 'NextSpmanStore',
            itemId:'nextSpmanId',
            name:'spman',
            fieldLabel: '审批人',
            displayField : 'realname',
            queryMode: "local",
            editable:false,
            valueField : 'userid',
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
        }
        ,{ text: '完成',itemId:'billApproveFormSubmit'}
        ,{ text: '退回',itemId:'billApproveFormZz'}
        ,{ text: '关闭',itemId:'billApproveFormClose'}
    ]
});