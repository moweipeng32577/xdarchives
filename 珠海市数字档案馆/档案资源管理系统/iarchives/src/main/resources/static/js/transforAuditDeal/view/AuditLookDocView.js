/**
 * Created by Administrator on 2019/10/26.
 */


Ext.define('TransforAuditDeal.view.AuditLookDocView', {
    extend: 'Ext.window.Window',
    xtype: 'auditLookDocView',
    itemId: 'auditLookDocViewid',
    title: '移交单据',
    width: 780,
    height: 380,
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
        fieldDefaults: {
            labelWidth: 120
        },
        items: [{
            xtype:'textfield',
            fieldLabel: '',
            name: 'docid',
            hidden: true
        }, {
            xtype: 'textfield',
            fieldLabel: '交接工作名称',
            name: 'transfertitle',
            editable:false
        },{
            xtype:'textarea',
            fieldLabel: '内容描述',
            name: 'transdesc',
            editable:false
        }, {
            layout: 'column',
            itemId:'multcolumnId',
            items: [{
                columnWidth: .47,
                items: [{
                    xtype: 'textfield',
                    fieldLabel: '移交人',
                    name: 'transuser',
                    editable:false,
                    style: 'width: 100%'
                }]
            },{
                columnWidth:.06,
                xtype:'displayfield'
            },{
                columnWidth: .47,
                items: [{
                    xtype: 'textfield',
                    fieldLabel: '载体起止顺序号',
                    name: 'sequencecode',
                    editable:false,
                    style: 'width: 100%'
                }]
            },{
                columnWidth: .47,
                items: [{
                    xtype: 'textfield',
                    fieldLabel: '移交电子档案数',
                    name: 'transcount',
                    editable:false,
                    style: 'width: 100%'
                }]
            },{
                columnWidth:.06,
                xtype:'displayfield'
            },{
                columnWidth: .47,
                items: [{
                    xtype: 'textfield',
                    fieldLabel: '移交数据量(M)',
                    name: 'transfersize',
                    editable:false,
                    style: 'width: 100%'
                }]
            },{
                columnWidth: .47,
                items: [{
                    xtype: 'textfield',
                    fieldLabel: '移交机构',
                    editable: false,
                    name: 'transorgan',
                    editable:false,
                    style: 'width: 100%'
                }]
            },{
                columnWidth:.06,
                xtype:'displayfield'
            }, {
                columnWidth: .47,
                items: [{
                    fieldLabel: '实体移交时间',
                    xtype: 'textfield',
                    name: 'transdate',
                    editable:false,
                    style: 'width: 100%'
                }]
            }]
        }]
    }],
    buttons: [{
        text: '关闭',
        itemId: 'closeBtnID'
    }]
});
