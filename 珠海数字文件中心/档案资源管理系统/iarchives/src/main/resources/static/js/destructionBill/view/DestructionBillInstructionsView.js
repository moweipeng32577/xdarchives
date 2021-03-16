/**
 * Created by yl on 2017/12/7.
 */
Ext.define('DestructionBill.view.DestructionBillInstructionsView', {
    extend: 'Ext.window.Window',
    xtype: 'destructionBillInstructionsView',
    title: '批示',
    width: 380,
    height: 330,
    modal: true,
    resizable: false,
    closeToolText:'关闭',
    layout: 'fit',
    items: [
        {
            xtype: 'form',
            layout: {
                type: 'vbox',
                align: 'stretch'
            },
            bodyPadding: 10,
            items: [
            //     {
            //     xtype: 'textfield',
            //     fieldLabel: '审批人',
            //     editable: false,
            //     name: 'username',
            //     labelWidth: 60
            // },
                {
                xtype: 'textarea',
                height: '100%',
                // fieldLabel: '批示',
                editable: false,
                name: 'approve',
                margin:'15',
                flex: 2,
                value:'暂无批示信息',
                labelWidth: 60,
                width: 340,
                height: 250,
            }]
        }
    ]
});