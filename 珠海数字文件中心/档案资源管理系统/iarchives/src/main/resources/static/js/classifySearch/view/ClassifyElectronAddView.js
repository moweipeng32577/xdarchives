/**
 * Created by yl on 2017/11/3.
 */
Ext.define('ClassifySearch.view.ClassifyElectronAddView', {
    extend: 'Ext.window.Window',
    xtype: 'classifyElectronAddView',
    height: '100%',
    width: '100%',
    draggable: false,//禁止拖动
    resizable: false,//禁止缩放
    modal: true,
    closeToolText:'关闭',
    title: '电子查档',
    closeAction: 'hide',
    requires: [
        'Ext.layout.container.Border'
    ],
    layout: 'border',
    items: [{xtype: 'classifyElectronFormView'}, {xtype: 'classifyElectronFormGridView'}]
});