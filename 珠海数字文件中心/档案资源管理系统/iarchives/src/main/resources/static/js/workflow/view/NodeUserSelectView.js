/**
 * Created by Administrator on 2017/10/23 0023.
 */
Ext.define('Workflow.view.NodeUserSelectView', {
    extend: 'Ext.window.Window',
    xtype: 'nodeUserSelectView',
    itemId:'nodeUserSelectViewId',
    title: '设置环节用户',
    width:1000,
    height:500,
    bodyPadding: 20,
    layout:'hbox',
    modal:true,
    closeToolText:'关闭',
    items:[{
        flex:1,
        height:'100%',
        layout:'border',
        itemId:'organTreeAndSearchId',
        defaults: {
            split: true
        },
        items:[{
            region: 'north',
            height:'15%',
            layout:'form',
            items:[{
                xtype: 'searchfield',
                fieldLabel: '用户名',
                labelWidth: 85,
                itemId:'usernameSearchId'
            }]
        },{
            region: 'center',
            xtype:'organTreeView'
        }]
    },{
        flex:2,
        height:'100%',
        xtype: 'itemselector',
        imagePath: '../ux/images/',
        store: 'NodeUserSelectStore',
        displayField: 'realname',
        valueField: 'userid',
        allowBlank: false,
        msgTarget: 'side',
        fromTitle: '可选用户(按Ctrl+F查找)',
        toTitle: '已选用户'
    }],
    buttons: [
        { text: '提交',itemId:'nodeSelectSubmit'},
        { text: '关闭',itemId:'nodeSelectClose'}
    ]
});