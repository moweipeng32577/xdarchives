/**
 * Created by tanly on 2017/11/13 0013.
 */
Ext.define('Template.view.TemplateFieldSortView', {
    extend: 'Ext.window.Window',
    xtype: 'templateFieldSortView',
    itemId:'templateFieldSortViewId',
    title: '调整排序',
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
        store: 'TemplateFieldStore',
        displayField: 'fieldname',
        valueField: 'fieldcode',
        allowBlank: false,
        msgTarget: 'side',
        fromTitle: '模板字段',
        toTitle: '检索字段'
    }],
    buttons: [
        { text: '提交',itemId:'templateSortSubmit'},
        { text: '关闭',itemId:'templateSortClose'}
    ]
});