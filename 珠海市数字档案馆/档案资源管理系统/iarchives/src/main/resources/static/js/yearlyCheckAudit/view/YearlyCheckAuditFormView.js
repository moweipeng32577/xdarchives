/**
 * Created by Administrator on 2020/10/15.
 */



Ext.define('YearlyCheckAudit.view.YearlyCheckAuditFormView',{
    extend: 'Ext.form.Panel',
    xtype: 'yearlyCheckAuditFormView',
    itemId:'yearlyCheckAuditFormViewId',
    region: 'center',
    title:'审核单据',
    autoScroll: true,
    fieldDefaults: {
        labelWidth: 70
    },
    layout:'column',
    bodyPadding: 15,
    items:[{
        xtype: 'textfield',
        name:'id',
        hidden:true
    },{
        columnWidth: .47,
        xtype: 'textfield',
        itemId: 'submiterId',
        fieldLabel: '提交人',
        name: 'submiter',
        allowBlank: false,
        labelWidth: 85,
        readOnly:true
    }, {
        columnWidth: .06,
        xtype: 'displayfield'
    }, {
        columnWidth: .47,
        xtype: 'textfield',
        itemId: 'submittimeId',
        fieldLabel: '提交时间',
        allowBlank: false,
        name: 'submittime',
        labelWidth: 85,
        readOnly:true
    }, {
        columnWidth: 1,
        xtype: 'textfield',
        fieldLabel: '备注',
        labelWidth: 85,
        name:'remark',
        margin: '5 0 0 0',
        height:30,//文本框默认高度为30
        readOnly:true
    },{
        columnWidth: 1,
        itemId:'approveId',
        xtype: 'textarea',
        fieldLabel: '批示',
        labelWidth: 85,
        name:'approve',
        flex: 1,
        margin: '5 0 0 0',
        readOnly:true
    }],
    buttons:[{
        text: '添加批示',
        itemId:'approveAdd'
    }, {
        xtype : 'combo',
        store : 'NextNodeStore',
        itemId:'nextNodeId',
        name:'nextNode',
        fieldLabel: '下一环节',
        labelWidth: 85,
        displayField : 'text',
        editable:false,
        valueField : 'id',
        queryMode: 'local',
        style: "margin-left:24px",
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
                var nextSpman = combo.findParentByType("yearlyCheckAuditFormView").down("[itemId=nextSpmanId]");
                nextSpman.select(null);
                nextSpman.getStore().proxy.extraParams.nodeId = record.get('id');
                nextSpman.getStore().reload();
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
        valueField : 'userid',
        queryMode: 'local',
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
            }
        }
    },{
        text: '完成',
        itemId:'approveFormSubmit'
    }, {
        text: '退回',
        itemId:'approveFormBack'
    },{
        text: '关闭',
        itemId:'approveFormClose'
    }]
});
