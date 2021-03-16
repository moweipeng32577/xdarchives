/**
 * Created by tanly on 2017/11/3 0003.
 */
Ext.define('Acquisition.view.filing.OrderSettingSelectedFormView', {
    extend: 'Ext.form.Panel',
    xtype: 'ordersettingSelectedFormView',
    itemId: 'ordersettingSelectedFormViewID',
    title: '档号排序设置',
    header: false,
    bodyPadding: '5 110 25 20',
    layout: 'fit',
    modal: true,
    viewConfig: {
        autoFill: true
    },

    items: [{
        layout: 'border',
        itemId: 'SelectedborderItemid',
        items: [{
            region: 'center',
            itemId: 'itemselectorItemID',
            xtype: 'ordersettingItemSelectedFormView'
        }, {
            region: 'east',
            margin: '20',
            bodyPadding: '5 5 5 5',
            itemId: 'ordersettingDetailFormViewItemID',
            xtype: 'ordersettingDetailFormView'

        }]
    }],
    buttons: [{
        text: '保存',
        itemId: 'ordersettingSaveTwoBtnId'
    }]
});

