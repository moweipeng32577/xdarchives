/**
 * Created by yl on 2017/11/1.
 */
Ext.define('User.view.UserResetPWWin', {
    extend: 'Ext.window.Window',
    xtype: 'userResetPWWin',
    title: '初始化密码',
    width: 300,
    height: 145,
    modal: true,
    resizable: false,
    closeToolText:'关闭',
    layout: 'fit',
    items: [
        {
            xtype: 'form',
            layout: {
                type: 'vbox',
                align: 'stretch'
            },
            bodyPadding: 10,
            items: [{
                xtype: 'textfield',
                fieldLabel: '密码<span style="color: #CC3300; padding-right: 2px;">*</span>',
                allowBlank: false,
                blankText: '该输入项为必输项',
                name: 'loginpassword',
                labelWidth: 80,
                inputType: 'password',
                emptyText: '请输入初始密码'
            }]
        }
    ],
    buttons: [{
        itemId: 'submitBtnID',
        text: '提交'
    }, {
        itemId: 'closeBtnID',
        text: '关闭'
    }]
});