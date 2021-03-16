/**
 * Created by tanly on 2018/1/12 0012.
 */
Ext.define('User.view.UserGroupSetView', {
    extend: 'Ext.window.Window',
    itemId: 'selectorID',
    xtype: 'UserGroupSetView',
    title: '设置用户组',
    width:600,
    height:400,
    bodyPadding: 20,
    modal:true,
    closeToolText:'关闭',
    layout: 'fit',
    items: [{
        itemId: 'itemselectorID',
        xtype: 'itemselector',
        imagePath: '../ux/images/',
        store: 'UserGroupSetStore',
        displayField: 'rolename',
        valueField: 'roleid',
        allowBlank: false,
        msgTarget: 'side',
        fromTitle: '可选用户组(按Ctrl+F查找)',
        toTitle: '已选用户组',
        buttons:['add', 'remove']
    }],
    buttons: [
        { text: '保存',itemId:'save'},
        { text: '关闭',itemId:'close'}
    ]
});