/**
 * Created by tanly on 2017/10/26 0026.
 */
Ext.define('Nodesetting.view.NodesettingDetailFormView', {
    extend: 'Ext.window.Window',
    xtype: 'nodesettingDetailFormView',
    itemId: 'nodesettingDetailFormViewid',
    title: '增加机构节点',
    width: 780,
    height: 220,
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
            name: 'nodeid',
            hidden: true,
            itemId: 'nodeiditemid'
        }, {
            xtype:'textfield',
            fieldLabel: '',
            name: 'parentnodeid',
            hidden: true,
            itemId: 'parentnodeiditemid'
        }, {
            xtype:'textfield',
            fieldLabel: '',
            name: 'level',
            hidden: true,
            itemId: 'levelitemid'
        }, {
            xtype:'textfield',
            fieldLabel: '',
            name: 'nodetype',
            hidden: true,
            itemId: 'nodetypeitemid'
        }, {
            xtype:'textfield',
            fieldLabel: '',
            name: 'refid',
            hidden: true,
            itemId: 'refiditemid'
        }, {
            layout: 'column',
            itemId:'multcolumnId',
            items: [{
                columnWidth: .84,
                items: [{
                    xtype: 'nodesettingTreeComboboxView',
                    fieldLabel: '节点名称',
                    editable: false,
                    url: '/nodesetting/getOrganByParentId',
                    extraParams: {pcid: '0'},
                    allowBlank: false,
                    name: 'nodename',
                    itemId: 'nodenameitemid',
                    style: 'width: 100%'
                }]
            }, {
                columnWidth: .16,
                items: [{
                    xtype: 'checkbox',
                    boxLabel: '包含子节点',
                    itemId: 'containchilditemid',
                    style: 'width: 100%;margin-left:20px'
                }]
            }]
        }, {
            xtype: 'textfield',
            fieldLabel: '节点编码',
            name: 'nodecode',
            itemId: 'nodecodeitemid',
            allowBlank: false
        },{
            itemId: 'leafitemid',
            xtype: 'textfield',
            fieldLabel: '叶子节点',
            name: 'leaf',
            hidden: true,
            value:"true"
        }
        ]
    }]
    ,
    buttons: [{
        text: '保存',
        itemId: 'nodesettingSaveBtnID'
    }, {
        text: '取消',
        itemId: 'nodesettingCancelBtnID'
    }
    ]
});