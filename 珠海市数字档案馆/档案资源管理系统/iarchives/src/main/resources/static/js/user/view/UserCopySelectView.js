/**
 * Created by tanly on 2018/9/17 0024.
 */
Ext.define('User.view.UserCopySelectView', {
    extend: 'Ext.window.Window',
    xtype: 'userCopySelectView',
    itemId: 'userCopySelectViewId',
    title: '',
    width: '52%',
    height: '90%',
    bodyPadding: 5,
    layout: 'border',
    modal: true,
    closeToolText: '关闭',
    maxCount:0,//保存权限复制最大人数
    items: [{
        xtype: 'panel',
        height: 60,
        region: 'north',
        layout: {
            align: 'middle',
            pack: 'center',
            type: 'hbox'
        },
        items: [{
            xtype: 'label',
            itemId: 'totalText',
            text: '复制内容：',
            style: {color: 'red'},
            margin: 3
        }, {
            xtype: 'checkbox',
            boxLabel: '数据权限',
            itemId: 'dataCheck',
            margin: 10
        }, {
            xtype: 'checkbox',
            boxLabel: '机构权限',
            itemId: 'organCheck',
            margin: 10
        }, {
            xtype: 'checkbox',
            boxLabel: '功能权限',
            itemId: 'fnCheck',
            margin: 10
        }, {
            xtype: 'checkbox',
            boxLabel: '角色权限',
            itemId: 'roleCheck',
            margin: 10
        }, {
            xtype: 'checkbox',
            boxLabel: '工作流节点权限',
            itemId: 'nodeCheck',
            margin: 10
        }, {
            xtype: 'checkbox',
            boxLabel: '文件权限',
            itemId: 'fileCheck',
            margin: 10
        },{
            columnWidth: .65,
            xtype: 'label',
            text: '温馨提示：不能超过xxx人',
            itemId: 'maxMsg',
            //hidden:true,
            style:{
                color:'red',
                'font-size':'17px',
                'font-weight':'bold'
            },
            margin:'40 0 15 0'
        }]
    }, {
        xtype: 'panel',
        region: 'center',
        margin: '10 0 0 0',
        layout: 'hbox',
        items: [{
            flex: 1,
            height: '100%',
            layout:'border',
            itemId:'organTreeAndSearchId',
            defaults: {
                split: true
            },
            items:[{
                region: 'north',
                height:'10%',
                layout:'form',
                items:[{
                    xtype: 'searchfield',
                    fieldLabel: '用户名',
                    labelWidth: 85,
                    itemId:'usernameSearchId'
                }]
            },{
                region: 'center',
                xtype:'userOrganTreeView'
            }]
        }, {
            flex: 2,
            height: '100%',
            xtype: 'itemselector',
            imagePath: '../ux/images/',
            store: 'UserCopySelectStore',
            displayField: 'text',
            valueField: 'fnid',
            allowBlank: false,
            msgTarget: 'side',
            fromTitle: '可选(按Ctrl+F查找)',
            toTitle: '<span class="copyFunSelect">已选</span>'

        }]
    }],
    buttons: [
        {text: '全选', itemId: 'allOrNotSelect'},
        {text: '提交', itemId: 'copySelectSubmit'},
        {text: '关闭', itemId: 'copySelectClose'}
    ]
});