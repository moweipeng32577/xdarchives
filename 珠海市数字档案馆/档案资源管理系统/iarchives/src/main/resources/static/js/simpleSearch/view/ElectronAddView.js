/**
 * Created by yl on 2017/11/3.
 */
Ext.define('SimpleSearch.view.ElectronAddView', {
    extend: 'Ext.window.Window',
    xtype: 'electronAddView',
    height: '100%',
    width: '100%',
    draggable: false,//禁止拖动
    resizable: false,//禁止缩放
    modal: true,
    closeToolText:'关闭',
    title: '查档申请',
    closeAction: 'hide',
    closable:false,
    requires: [
        'Ext.layout.container.Border'
    ],
    layout: 'border',
    items: [{xtype: 'electronFormView'}, {xtype: 'electronFormGridView'}]
});