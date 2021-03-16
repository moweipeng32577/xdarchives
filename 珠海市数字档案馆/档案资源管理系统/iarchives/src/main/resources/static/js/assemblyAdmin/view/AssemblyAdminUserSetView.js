/**
 * Created by Administrator on 2019/7/3.
 */


Ext.define('AssemblyAdmin.view.AssemblyAdminUserSetView', {
    extend: 'Ext.window.Window',
    xtype: 'assemblyAdminUserSetView',
    itemId:'assemblyAdminUserSetViewId',
    title: '',
    width:800,
    height:500,
    bodyPadding: 20,
    layout:'hbox',
    modal:true,
    closeToolText:'关闭',
    items:[{
        flex:1,
        height:'100%',
        xtype:'assemblyAdminLeftView'
    },{
        flex:2,
        height:'100%',
        xtype: 'itemselector',
        imagePath: '../ux/images/',
        store: 'AssemblyUserSetStore',
        displayField: 'realname',
        valueField: 'userid',
        allowBlank: false,
        msgTarget: 'side',
        fromTitle: '可选用户(按Ctrl+F查找)',
        toTitle: '已选用户'
    }],
    buttons: [
        { text: '提交',itemId:'setUserSubmit'},
        { text: '关闭',itemId:'setUserClose'}
    ]
});
