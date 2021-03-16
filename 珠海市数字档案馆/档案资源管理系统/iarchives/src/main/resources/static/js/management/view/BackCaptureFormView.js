/**
 * Created by Administrator on 2019/10/30.
 */


Ext.define('Management.view.BackCaptureFormView', {
    extend: 'Ext.window.Window',
    xtype: 'backCaptureFormView',
    itemId: 'backCaptureFormViewId',
    title: '退回采集单据',
    width: 780,
    height: 310,
    modal: true,
    closeToolText:'关闭',
    closeAction:"hide",
    layout: 'fit',
    items: [{
        layout: {
            type: 'vbox',
            align: 'stretch'
        },
        xtype: 'form',
        itemId: 'formId',
        margin: '22',
        fieldDefaults: {
            labelWidth: 80
        },
        items: [{
            xtype:'textfield',
            fieldLabel: '',
            name: 'id',
            hidden: true
        }, {
            xtype:'textarea',
            fieldLabel: '退回原因',
            name: 'backreason',
            allowBlank: false
        }, {
            layout: 'column',
            itemId:'multcolumnId',
            items: [{
                columnWidth: .34,
                items: [{
                    xtype: 'textfield',
                    fieldLabel: '退回人',
                    allowBlank: false,
                    name: 'backer',
                    style: 'width: 100%'
                }]
            },{
                columnWidth:.06,
                xtype:'displayfield'
            },{
                columnWidth: .34,
                items: [{
                    fieldLabel: '退回时间',
                    allowBlank: false,
                    xtype: 'textfield',
                    name: 'backtime',
                    style: 'width: 100%'
                }]
            },{
                columnWidth:.06,
                xtype:'displayfield'
            },{
                columnWidth: .20,
                items: [{
                    xtype: 'textfield',
                    fieldLabel: '数量',
                    allowBlank: false,
                    name: 'backcount',
                    style: 'width: 100%'
                }]
            },{
                columnWidth: 1,
                items: [{
                    xtype: 'textfield',
                    fieldLabel: '退回机构',
                    name: 'backorgan',
                    style: 'width: 100%'
                }]
            }]
        }]
    }],
    buttons: [{
        text: '退回',
        itemId: 'backCapture'
    },{
        text: '关闭',
        itemId: 'closeBtnID'
    }]
});
