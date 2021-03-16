/**
 * Created by Administrator on 2020/7/21.
 */
Ext.define('DepartmentAudit.view.ProjectAddLookView', {
    extend: 'Ext.window.Window',
    xtype: 'projectAddLookView',
    height: '100%',
    width: '100%',
    draggable: false,//禁止拖动
    resizable: false,//禁止缩放
    modal: true,
    closeToolText:'关闭',
    title: '查看项目',
    closeAction: 'hide',
    closable:false,
    requires: [
        'Ext.layout.container.Border'
    ],
    layout: 'border',
    items: [{xtype: 'projectAddLookFormView'}, {xtype: 'projectLogLookGridView'}]
});