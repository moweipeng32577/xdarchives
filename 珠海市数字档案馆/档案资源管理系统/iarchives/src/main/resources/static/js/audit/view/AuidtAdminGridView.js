/**
 * Created by Administrator on 2019/10/28.
 */

Ext.define('Audit.view.AuidtAdminGridView',{
    extend:'Comps.view.BasicGridView',
    xtype:'auidtAdminGridView',
    title: '移交单据',
    region: 'center',
    searchstore:[
        {item: "transuser", name: "移交人"},
        {item: "transdesc", name: "移交说明"},
        {item: "transorgan", name: "移交机构"}
    ],
    store:'AuditAdminGridStore',
    tbar: [{
        text:'查看单据记录',
        iconCls:'fa fa-eye',
        itemId:'lookAuditDoc'
    },'-',{
        text:'办理',
        iconCls:'fa fa-table',
        itemId:'auditDeal'
    },'-',{
        text:'查看退回原因',
        iconCls:'fa fa-reply-all',
        itemId:'lookBack'
    }, '-',{
        itemId:'print',
        xtype: 'button',
        text: '打印'
    },'-',{
        xtype: 'button',
        text: '签章',
        itemId:'signId'
    }],
    columns: [
        {text: '移交说明', dataIndex: 'transdesc', flex: 3, menuDisabled: true},
        {text: '移交人', dataIndex: 'transuser', flex: 1, menuDisabled: true},
        {text: '移交机构', dataIndex: 'transorgan', flex: 2, menuDisabled: true},
        {text: '数量', dataIndex: 'transcount', flex: 1, menuDisabled: true},
        {text: '移交时间', dataIndex: 'transdate', flex: 2, menuDisabled: true},
        {text: '审核人', dataIndex: 'approveman', flex: 1, menuDisabled: true,itemId:'approvemanId'},
        {text: '审核时间', dataIndex: 'approvetime', flex: 2, menuDisabled: true,itemId:'approvetimeId'},
        {text: '移交签章', dataIndex: 'transforcasign', flex: 2, menuDisabled: true,itemId:'transforcasignId'},
        {text: '审核签章', dataIndex: 'editcasign', flex: 2, menuDisabled: true,itemId:'editcasignId'},
        {text: '所属节点', dataIndex: 'nodefullname', flex: 5, menuDisabled: true}
    ]
});
