/**
 * Created by tanly on 2017/11/13 0013.
 */
Ext.define('Template.view.TemplateSelectionView', {
    extend: 'Ext.window.Window',
    xtype: 'templateSelectionView',
    itemId:'templateSelectionViewId',
    title: '字段管理',
    width:600,
    height:'75%',
    bodyPadding: '15 40 15 40',
    layout:'fit',
    modal:true,
    closeToolText:'关闭',
    items:[{
        xtype: 'itemselector',
        anchor: '100%',
        imagePath: '../ux/images/',
        store: 'TemplateSelectStore',
        displayField: 'fieldname',
        valueField: 'fieldcode',
        allowBlank: false,
        msgTarget: 'side',
        fromTitle: '可选字段(按Ctrl+F查找)',
        toTitle: '已选字段'
    }],
    buttons: [{ text: '配置表单其它属性',itemId:'editOtherOptionsBtn',hidden:true},
        { text: '提交',itemId:'templateSelectSubmit'},
        { text: '关闭',itemId:'templateSelectClose'}
    ]
});