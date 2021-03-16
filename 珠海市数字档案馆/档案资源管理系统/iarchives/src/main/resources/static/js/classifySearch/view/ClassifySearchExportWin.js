/**
 * Created by RonJiang on 2017/11/1 0001.
 */
Ext.define('ClassifySearch.view.ClassifySearchExportWin', {
    extend: 'Ext.window.Window',
    xtype: 'classifySearchExportWin',
    itemId:'classifySearchExportWinId',
    title: 'excel导出',
    frame: true,
    closeToolText:'关闭',
    resizable: false,//是否可以改变窗口大小
    width: 610,
    minWidth: 610,
    // minHeight: 200,
    modal:true,
    layout: {
        type: 'vbox',
        align: 'stretch'
    },

    defaults: {
        layout: 'form'
        // xtype: 'container',
        // style: 'width: 50%'
    },
    items: [{
        xtype: 'form',
        itemId:'classifySearchExportFileNameFormId',
        modelValidation: true,
        items: [
            { xtype: 'textfield',fieldLabel: '',name:'entryid',hidden:true},
            {
                xtype: 'textfield',
                fieldLabel:'请输入excel名称',
                itemId:'classifySearchExportFileNameId',
                allowBlank: false,
               /* vtype:'alphanum',
                vtypeText:'只能是数字或字母'*/
            }
        ]
    }],
    buttons: [
        { text: '确定',itemId:'classifySearchExportBtnId'},
        { text: '关闭',itemId:'classifySearchExportCloseBtnId',margin:'0 190 0 40'}
    ]
});