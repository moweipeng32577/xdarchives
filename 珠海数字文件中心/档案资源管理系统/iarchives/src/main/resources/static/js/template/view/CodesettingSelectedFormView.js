/**
 * Created by tanly on 2017/11/3 0003.
 */
Ext.define('Template.view.CodesettingSelectedFormView', {
    extend: 'Ext.form.Panel',
    xtype: 'codesettingSelectedFormView',
    itemId: 'codesettingSelectedFormViewID',
    title: '档号设置',
    bodyPadding: '50 110 50 100',
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
            xtype: 'codesettingItemSelectedFormView'
        }, {
            region: 'east',
            margin: '20',
            bodyPadding: '50 5 50 5',
            itemId: 'codesettingDetailFormViewItemID',
            xtype: 'codesettingDetailFormView'

        }]
    }],
    buttons: [{
        text: '保存',
        itemId: 'codesettingSaveBtnId'
    },{
        text:'返回',
        itemId:'back'
    }]
});

