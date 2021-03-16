/**
 * Created by SunK on 2018/10/11 0011.
 */
Ext.define('Appraisal.view.AppraisalGroupSetView', {
    extend: 'Ext.window.Window',
    itemId: 'selectorID',
    xtype: 'appraisalGroupSetView',
    title: '选择字段',
    width:600,
    height:500,
    bodyPadding: 50,
    modal:true,
    closeToolText:'关闭',
    layout: 'fit',
    items: [{
        itemId: 'itemselectorID',
        xtype: 'itemselector',
        imagePath: '../ux/images/',
        store: 'AppraisalGroupSetStore',
        displayField: 'fieldname',
        valueField: 'fieldcode',
        allowBlank: false,
        msgTarget: 'side',
        fromTitle: '可选字段(按Ctrl+F查找)',
        toTitle: '已选字段',
        buttons:['add', 'remove']
    }],
    buttons: [
        {text: '全选',itemId:'addAllOrNotAll'},
        { text: '保存',itemId:'save'},
        { text: '关闭',itemId:'close'}
    ]
});