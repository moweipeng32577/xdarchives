/**
 * Created by tanly on 2017/12/5.
 */

Ext.define('OpenApprove.view.OpenApproveFormView',{
    extend: 'Ext.form.Panel',
    xtype: 'openApproveFormView',
    itemId:'openApproveFormViewId',
    region: 'center',
    title:'审核单据',
    autoScroll: true,
    fieldDefaults: {
        labelWidth: 100
    },
    layout:'column',
    bodyPadding: 15,
    items:[
        { columnWidth: 1,xtype: 'textfield',name:'id',hidden:true},
        {
            columnWidth: .98,
            xtype: 'textfield',
            fieldLabel: '单据题名',
            name:'doctitle',
            itemId:'doctitleItem',
            readOnly:true
        },{
            columnWidth:.02,
            xtype:'displayfield'
        },{
            columnWidth: .48,
            xtype: 'textfield',
            fieldLabel: '送审人',
            itemId:'submitterItem',
            name:'submitter',
            margin:'10 0 0 0',
            readOnly:true
        },{
            columnWidth:.02,
            xtype:'displayfield'
        },{
            columnWidth: .48,
            xtype: 'textfield',
            fieldLabel: '开放批次号',
            name:'batchnum',
            margin:'10 0 0 0',
            editable:false
        },{
            columnWidth:.02,
            xtype:'displayfield'
        } ,{
            columnWidth: .48,
            fieldLabel: '送审时间',
            xtype: 'textfield',
            name: 'submitdate',
            format: 'Ymd',
            margin:'10 0 0 0',
            readOnly:true
        },{
            columnWidth:.02,
            xtype:'displayfield'
        },{
            columnWidth: .48,
            xtype: 'textfield',
            fieldLabel: '条目总数',
            name:'entrycount',
            margin:'10 0 0 0',
            readOnly:true
        },{
            columnWidth:.02,
            xtype:'displayfield'
        },
        // {
        //     columnWidth: .48,
        //     xtype : 'textfield',
        //     name:'opentype',
        //     fieldLabel: '开放类型',
        //     itemId:'opentypeItem',
        //     // editable:false,
        //     margin:'10 0 0 0',
        //     readOnly:true
        // },{
        //     columnWidth:.02,
        //     xtype:'displayfield'
        // },
        {
            columnWidth: .48,
            xtype: 'textfield',
            fieldLabel: '备注信息',
            name:'remarks',
            margin:'10 0 0 0',
            readOnly:true
        },{
            columnWidth:.02,
            xtype:'displayfield'
        },{
            columnWidth: .98,
            itemId:'approveId',
            xtype: 'textarea',
            fieldLabel: '批示',
            name:'approve',
            flex: 1,
            margin: '10 0 0 0',
            readOnly:true
        },{
            columnWidth:.02,
            xtype:'displayfield'
        }
    ]
    ,buttons:[{
        xtype: 'checkbox',
        itemId:'sendmsgId',
        inputValue : true,
        fieldLabel:'发送短信',
        margin: '0 30 0 0',
        hidden:openSendmsg==true?false:true
    }, { text: '添加批示',
        itemId:'approveAdd'
    }, {
        xtype: 'combo',
        store: 'NextNodeStore',
        itemId: 'nextNodeId',
        name: 'spman',
        fieldLabel: '下一环节',
        displayField: 'text',
        editable: false,
        valueField: 'id',
        queryMode: "local",
        style: "margin-left:24px",
        listeners: {
            afterrender: function (combo) {
                var store = combo.getStore();
                store.load(function (data) {
                    if (this.getCount() > 0) {
                        combo.select(this.getAt(0));
                    }
                });
            },
            select: function (combo, record) {
                var spmanOrgan = combo.findParentByType("openApproveFormView").down("[itemId=approveOrgan]");
                spmanOrgan.select(null);
                var nextSpman = combo.findParentByType("openApproveFormView").down("[itemId=nextSpmanId]");
                nextSpman.select(null);
                nextSpman.getStore().removeAll();
                nextSpman.getStore().proxy.extraParams.nodeId = record.get('id');
                spmanOrgan.getStore().proxy.extraParams.type = "approve"; //审批时获取审批单位
                spmanOrgan.getStore().proxy.extraParams.taskid = taskid;
                spmanOrgan.getStore().proxy.extraParams.nodeid = record.get('id');
                spmanOrgan.getStore().proxy.extraParams.worktext = null;
                spmanOrgan.getStore().proxy.extraParams.approveType = "dataOpen"; //审批类型
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
                var spmancombo = combo.findParentByType("openApproveFormView").down("[itemId=nextSpmanId]");
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
            editable:false,
            queryMode:'local',
            style: "margin-left:12px",
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
        ,{ text: '完成',itemId:'openApproveFormSubmit'}
        ,{ text: '退回',itemId:'openApproveFormZz'}
        ,{ text: '退回上一环节',itemId:'openApproveBackPre'}
        ,{ text: '关闭',itemId:'openApproveFormClose',style:"margin-left:5px"}
    ]
});