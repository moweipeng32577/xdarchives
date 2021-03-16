/**
 * Created by tanly on 2018/4/20 0020.
 */
Ext.define('User.view.UserMoveSelectView', {
    extend: 'Ext.window.Window',
    xtype: 'userMoveSelectView',
    itemId: 'UserMoveSelectViewId',
    title: '用户移动',
    width: 780,
    height: 200,
    modal: true,
    closeToolText: '关闭',
    layout: {
        type: 'vbox',
        align: 'stretch'
    },

    defaults: {
        layout: 'form',
        xtype: 'container',
        defaultType: 'textfield',
        style: 'width: 50%'
    },
    items: [{
        xtype: 'form',
        margin: '25',
        modelValidation: true,
        items: [{
            xtype:'textfield',
            fieldLabel: '',
            name: 'refid',
            hidden: true,
            itemId: 'refiditemid'
        },{
            xtype: 'userTreeComboboxView',
            fieldLabel: '移动用户到',
            editable: false,
            url: '/nodesetting/getOrganByParentId',
            allowBlank: false,
            name: 'orgname',
            itemId: 'orgnameitemid',
            style: 'width: 100%'
        }]
    }],
    buttons: [{
        text: '保存',
        itemId: 'save'
    }, {
        text: '取消',
        itemId: 'cancel'
    }]
});