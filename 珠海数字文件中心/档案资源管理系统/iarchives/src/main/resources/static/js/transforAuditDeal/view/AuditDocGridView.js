/**
 * Created by Administrator on 2019/10/25.
 */


Ext.define('TransforAuditDeal.view.AuditDocGridView',{
    extend:'Comps.view.BasicGridView',
    xtype:'auditDocGridView',
    header:false,
    searchstore:[
        {item: "transuser", name: "移交人"},
        {item: "transdesc", name: "移交说明"},
        {item: "transorgan", name: "移交机构"}
    ],
    store:'AuditDocGridStore',
    tbar: [{
        text:'查看',
        iconCls:'fa fa-eye',
        itemId:'look'
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
                var spmanOrgan = combo.findParentByType("auditDocGridView").down("[itemId=approveOrgan]");
                spmanOrgan.select(null);
                var nextSpman = combo.findParentByType("auditDocGridView").down("[itemId=nextSpmanId]");
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
                var spmancombo = combo.findParentByType("auditDocGridView").down("[itemId=nextSpmanId]");
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
        itemId:'move'
    },'-',{
        text:'退回',
        iconCls:'fa fa-reply-all',
        itemId:'sendback'
    },'-',{
        text:'返回',
        iconCls:'fa fa-reply-all',
        itemId:'back'
    }],
    columns: [
        {text: '移交说明', dataIndex: 'transdesc', flex: 3, menuDisabled: true},
        {text: '移交人', dataIndex: 'transuser', flex: 1, menuDisabled: true},
        {text: '移交机构', dataIndex: 'transorgan', flex: 2, menuDisabled: true},
        {text: '数量', dataIndex: 'transcount', flex: 1, menuDisabled: true},
        {text: '移交时间', dataIndex: 'transdate', flex: 2, menuDisabled: true},
        {text: '所属节点', dataIndex: 'nodefullname', flex: 5, menuDisabled: true}
    ]
});
