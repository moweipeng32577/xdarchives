/**
 * Created by yl on 2017/11/3.
 */
Ext.define('ExchangeTransfer.view.ExchangeTransferWindow', {
    extend: 'Ext.window.Window',
    xtype: 'exchangeTransferWindow',
    title: '移交数据',
    width: '25%',
    height: 150,
    modal: true,
    resizable: false,
    closeToolText:'关闭',
    layout: 'fit',
    items: [
        {
            xtype: 'form',
            layout: {
                type: 'hbox',
                align: 'center',
                pack: 'center'
            },
            bodyPadding: 2,
            items: [{
                xtype:'textfield',
                fieldLabel: '',
                name: 'refid',
                hidden: true,
                itemId: 'refiditemid'
            },{
                xtype: 'nodesettingTreeComboboxView',
                fieldLabel: '档案分类',
                editable: false,
                url: '/nodesetting/getNodeByParentId',
                extraParams: {pcid:''},
                allowBlank: false,
                name: 'nodename',
                itemId: 'nodenameitemid'
            }]
        }
    ],
    buttons: [{
        itemId:'confirmBtnID',
        text: '确定'
    }, {
        itemId:'closeBtnID',
        text: '关闭'
    }]
});