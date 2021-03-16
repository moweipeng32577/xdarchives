/**
 * Created by RonJiang on 2017/11/1 0001.
 */
Ext.define('OriginalSearch.view.OriginalSearchExportWin', {
    extend: 'Ext.window.Window',
    xtype: 'originalSearchExportWin',
    itemId:'originalSearchExportWinId',
    title: 'excel导出',
    frame: true,
    closeToolText:'关闭',
    resizable: false,//是否可以改变窗口大小
    width: 610,
    minWidth: 610,
    modal:true,
    layout: {
        type: 'vbox',
        align: 'stretch'
    },

    defaults: {
        layout: 'form'
    },
    items: [{
        xtype: 'form',
        itemId:'simpleSearchExportFileNameFormId',
        modelValidation: true,
        items: [
            { xtype: 'textfield',fieldLabel: '',name:'entryid',hidden:true},
            {
                xtype: 'textfield',
                fieldLabel:'请输入excel名称',
                itemId:'simpleSearchExportFileNameId',
                allowBlank: false
                /*vtype:'allowBlank',
                vtypeText:'不能为空'*/
            }
        ]
    }],
    buttons: [
        { text: '确定',itemId:'simpleSearchExportBtnId'},
        { text: '关闭',itemId:'simpleSearchExportCloseBtnId',margin:'0 190 0 40'}
    ]
});