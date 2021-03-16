/**
 * Created by Administrator on 2019/12/26.
 */


Ext.define('ThematicProd.view.ThematicProdDocFormView', {
    extend: 'Ext.window.Window',
    xtype: 'thematicProdDocFormView',
    itemId: 'thematicProdDocFormViewid',
    title: '数据发布单据',
    width: 780,
    height: 480,
    modal: true,
    closeToolText: '关闭',
    closeAction: "hide",
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
            xtype: 'textfield',
            fieldLabel: '',
            name: 'docid',
            hidden: true
        }, {
            xtype: 'textfield',
            fieldLabel: '交接工作名称',
            name: 'transfertitle'
        }, {
            xtype: 'textarea',
            fieldLabel: '内容描述',
            name: 'transdesc',
            itemId: 'refiditemid'
        }, {
            layout: 'column',
            itemId: 'multcolumnId',
            items: [{
                columnWidth: .47,
                items: [{
                    xtype: 'textfield',
                    fieldLabel: '移交人',
                    name: 'transuser',
                    // itemId: 'nodenameitemid',
                    style: 'width: 100%'
                }]
            }, {
                columnWidth: .06,
                xtype: 'displayfield'
            }, {
                columnWidth: .47,
                items: [{
                    xtype: 'textfield',
                    fieldLabel: '载体起止顺序号',
                    name: 'sequencecode',
                    style: 'width: 100%'
                }]
            }, {
                columnWidth: .47,
                items: [{
                    xtype: 'textfield',
                    fieldLabel: '移交电子档案数',
                    name: 'transcount',
                    style: 'width: 100%'
                }]
            }, {
                columnWidth: .06,
                xtype: 'displayfield'
            }, {
                columnWidth: .47,
                items: [{
                    xtype: 'textfield',
                    fieldLabel: '移交数据量(M)',
                    name: 'transfersize',
                    style: 'width: 100%'
                }]
            }, {
                columnWidth: .47,
                items: [{
                    xtype: 'textfield',
                    fieldLabel: '移交部门',
                    editable: false,
                    name: 'transorgan',
                    style: 'width: 100%'
                }]
            }, {
                columnWidth: .06,
                xtype: 'displayfield'
            }, {
                columnWidth: .47,
                items: [{
                    fieldLabel: '实体移交时间',
                    xtype: 'textfield',
                    name: 'transdate',
                    format: 'Y-m-d H:i:s',
                    style: 'width: 100%',
                    editable: false,
                    value: new Date().format('yyyy-MM-dd hh:mm:ss')
                }]
            }]
        }, {
            xtype: 'textfield',
            fieldLabel: '移交载体数量',
            name: 'transferstcount'
        }]
    }]
    ,
    buttons: [{
        text: '发布',
        itemId: 'releaseID'
    }, {
        text: '取消',
        itemId: 'cancelID',handler: function (btn) {
            btn.up('thematicProdDocFormView').close();
        }
    }
    ]
});
