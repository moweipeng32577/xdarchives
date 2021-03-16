/**
 * Created by Administrator on 2020/7/27.
 */

Ext.define('User.view.FillingSortUserSelectView', {
    extend: 'Ext.window.Window',
    xtype: 'fillingSortUserSelectView',
    itemId:'fillingSortUserSelectViewId',
    title: '设置归档排序用户',
    width:1000,
    height:500,
    bodyPadding: 20,
    layout:'hbox',
    modal:true,
    closeToolText:'关闭',
    items:[{
        flex:1,
        height:'100%',
        xtype:'organTreeView'
    },{
        flex:2,
        height:'100%',
        xtype: 'itemselector',
        imagePath: '../ux/images/',
        store: 'FillingSortUserSelectStore',
        displayField: 'realname',
        valueField: 'userid',
        allowBlank: false,
        msgTarget: 'side',
        fromTitle: '可选用户(按Ctrl+F查找)',
        toTitle: '已选用户'
    }],
    buttons: [
        { text: '提交',itemId:'selectSubmit'},
        { text: '关闭',itemId:'selectClose'}
    ]
});
