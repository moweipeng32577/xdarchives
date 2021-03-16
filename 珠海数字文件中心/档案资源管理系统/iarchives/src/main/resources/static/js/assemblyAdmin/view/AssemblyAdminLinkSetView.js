/**
 * Created by Administrator on 2019/7/3.
 */



Ext.define('AssemblyAdmin.view.AssemblyAdminLinkSetView', {
    extend: 'Ext.panel.Panel',
    xtype: 'assemblyAdminLinkSetView',
    itemId:'assemblyAdminLinkSetViewId',
    bodyPadding: '20 100',
    layout:'hbox',
    items:[
        {
            flex:2,
            height:'100%',
            xtype: 'itemselector',
            imagePath: '../ux/images/',
            store: 'AssemblyAdminLinkSetStore',
            displayField: 'modelname',
            valueField: 'id',
            allowBlank: false,
            msgTarget: 'side',
            fromTitle: '可选环节(按Ctrl+F查找)',
            toTitle: '已选环节'
        }
    ],
    buttons: [
        { text: '提交',itemId:'linksetSubmit'},
        { text: '关闭',itemId:'linksetClose'}
    ]
});
