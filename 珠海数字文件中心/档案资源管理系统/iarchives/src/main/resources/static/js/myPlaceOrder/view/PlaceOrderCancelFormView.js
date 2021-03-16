/**
 * Created by Administrator on 2020/4/22.
 */


Ext.define('MyPlaceOrder.view.PlaceOrderCancelFormView', {
    extend: 'Ext.window.Window',
    xtype: 'placeOrderCancelFormView',
    itemId: 'placeOrderCancelFormViewId',
    title: '取消预约',
    autoScroll: true,
    frame: true,
    resizable: true,
    width: 600,
    height: '40%',
    modal: true,
    closeToolText: '关闭',
    layout: {
        type: 'vbox',
        align: 'stretch'
    },
    defaults: {
        layout: 'form',
        xtype: 'container',
        defaultType: 'textfield',
        style: 'width: 50%'
    },
    items: [{
        xtype: 'form',
        modelValidation: true,
        margin: '15',
        items: [
            {
                itemId: 'canceluserId',
                xtype: 'textfield',
                fieldLabel: '取消人',
                allowBlank: false,
                name: 'canceluser'
            }, {
                itemId: 'canceltimeId',
                xtype: 'textfield',
                fieldLabel: '取消时间',
                allowBlank: false,
                name: 'canceltime'
            },{
                itemId: 'cancelreasonId',
                xtype: 'textarea',
                fieldLabel: '取消原因',
                allowBlank: false,
                name: 'cancelreason'
            }
        ]
    }],
    buttons: [
        {text: '提交', itemId: 'cancelSubmit'},
        {text: '关闭', itemId: 'cancelClose'}
    ]
});


