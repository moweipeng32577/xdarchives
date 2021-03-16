/**
 * Created by yl on 2017/10/25.
 */
Ext.define('Import.view.ImportWindow', {
    extend: 'Ext.window.Window',
    xtype: 'importWindow',
    title: '数据导入',
    width: 300,
    height: 130,
    modal: true,
    resizable: false,
    closeToolText:'关闭',
    layout: 'fit',
    items: [
        {
            xtype: 'form',
            fileUpload: true,
            layout: {
                type: 'hbox',
                align: 'center',
                pack: 'center'
            },
            bodyPadding: 2,
            items: [{
                xtype: 'fileuploadfield',
                itemId:'importFileNameID',
                labelWidth: 80,
                width:285,
                fieldLabel: '选择文件',
                buttonText: '浏览...',
                name:'import'
            }],
            buttons: [{
                itemId:'importDownloadBtnID',
                text: '下载模版',
                margin:'0 22 0 0'
            }, {
                itemId:'importConfirmBtnID',
                text: '确定'
            }, {
                itemId:'importCloseBtnID',
                text: '关闭'
            }]
        }
    ],
    listeners:{

    }
});