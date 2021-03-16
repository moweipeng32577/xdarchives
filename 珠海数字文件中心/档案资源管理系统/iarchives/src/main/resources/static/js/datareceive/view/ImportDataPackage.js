/**
 * Created by Leo on 2020/7/22 0022.
 */
Ext.define('Datareceive.view.ImportDataPackage', {
    extend: 'Ext.panel.Panel',
    xtype:'importDataPackage',
    layout:'border',
    items:[{
            xtype: 'form',
            itemId:'formId',
            region: 'north',
            height: 95,
            layout: 'hbox',
            items: [{
                xtype: 'fieldset',
                title: '数据文件',
                margin: '0 5 5 5',
                flex: 1,
                layout: 'fit',
                items: [{
                    xtype: 'filefield',
                    clearOnSubmit:false,
                    name:'source',
                    buttonText:'打开',
                    allowBlank:false,
                    hideLabel: true
                }]
            }, {
                xtype: 'hidden',
                name: 'target'
            }]
    }],
    buttons: [{
        itemId: 'uploadID',
        text: '上传'
    }, {
        itemId: 'closeBtnID',
        text: '关闭'
    }]
})