/**
 * 安防控件
 * 设置布防、撤防
 * Created by Rong on 2019-11-20.
 */
Ext.define('Lot.view.AFSettingFormView', {
    extend: 'Ext.form.FormPanel',
    xtype: 'AFSettingForm',
    bodyPadding: 25,
    height: 70,
    layout: 'column',
    items: [{
        width: 100,
        xtype: 'button',
        text: '布防',
        itemId: 'start',
        margin: '0 0 0 30'
    }, {
        width: 100,
        xtype: 'button',
        text: '撤防',
        itemId: 'stop'
    }]
});