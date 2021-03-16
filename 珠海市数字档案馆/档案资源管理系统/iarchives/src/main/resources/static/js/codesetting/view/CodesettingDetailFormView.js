/**
 * Created by tanly on 2017/11/4 0004.
 */
Ext.define('Codesetting.view.CodesettingDetailFormView', {
    extend: 'Ext.form.Panel',
    title: '设置信息:',
    width: 300,
    height: 120,
    layout: 'form',
    xtype: 'codesettingDetailFormView',
    itemId: 'codesettingDetailFormViewID',
    autoScroll: true,
    items: [
        {
            xtype: 'textfield',
            itemId: 'areaid',
            fieldLabel: '域名描述',
            readOnly: true,
            maxLength: 32,
            name: 'areatext'
        }, {
            xtype: 'textfield',
            itemId: 'splitcodeid',
            fieldLabel: '分割符号',
            maxLength: 32,
            name: 'splitcodetext',
            enableKeyEvents: true
        }, {
            xtype: 'textfield',
            itemId: 'lengthid',
            name: 'lengthtext',
            maxLength: 32,
            enableKeyEvents: true,
            fieldLabel: '单位长度'
        }, {
            xtype: 'textfield',
            itemId: 'hiddenfieldId',
            name: 'hideid',
            hidden: true
        }
    ]
});