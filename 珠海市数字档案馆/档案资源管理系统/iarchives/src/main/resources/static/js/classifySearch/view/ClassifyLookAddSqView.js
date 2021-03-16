/**
 * Created by yl on 2017/11/3.
 */
Ext.define('ClassifySearch.view.ClassifyLookAddSqView', {
    extend: 'Ext.window.Window',
    xtype: 'classifylookAddSqView',
    height: '100%',
    width: '100%',
    draggable: false,//禁止拖动
    resizable: false,//禁止缩放
    modal: true,
    closeToolText:'关闭',
    title: '查档申请',
    closeAction: 'hide',
    layout: 'border',
    split:true,
    items: [{xtype: 'classifylookAddFormView'}, {xtype: 'classifylookAddFormGridView'}]
});