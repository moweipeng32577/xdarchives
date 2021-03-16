/**
 * Created by yl on 2017/10/25.
 */
Ext.define('Export.view.ExportCenterView', {
    extend: 'Ext.grid.Panel',
    xtype: 'exportCenterView',
    region: 'center',
    requires: [
        'Ext.ux.ProgressBarPager'
    ],
    store: 'ExportGridStore',
    columnLines: true,
    viewConfig: {
        autoFill: true
    },
    style: 'text-align:left;',
    align: 'left',
    selModel: Ext.create('Ext.selection.CheckboxModel', {
        injectCheckbox: 0,//checkbox位于哪一列，默认值为0
        enableHdMenu: false,
        selType: "checkboxmodel",
    }),
    columns: [
        {xtype: "rownumberer", flex: 0.5, align: 'center'},
        {text: '帐号', dataIndex: 'user', flex: 2, menuDisabled: true},
        {text: '姓名', dataIndex: 'userName', flex: 2, menuDisabled: true},
        {text: 'ip地址', dataIndex: 'ipAddress', flex: 2, menuDisabled: true},
        {text: '操作时间', dataIndex: 'operateTime', flex: 2, menuDisabled: true},
        {text: '操作描述', dataIndex: 'operateDes', flex: 3, menuDisabled: true},
        {text: '功能名称', dataIndex: 'operateFun', flex: 2, menuDisabled: true}
    ],
    bbar: {
        xtype: 'pagingtoolbar',//底部工具栏是分页工具栏
        store: 'ExportGridStore',
        displayInfo: true,//显示共XX页，每页显示XX条的信息
        autoScroll: true,
        beforePageText: "当前",
        afterPageText: "页,共 {0} 页",
        refreshText: "刷新",
        items: [{
            xtype: 'combobox',
            itemId: 'pagesizeComboID',
            name: 'pagesize',
            hiddenName: 'pagesize',
            store: new Ext.data.ArrayStore({
                fields: ['text', 'value'],
                data: [['5', 5], ['7', 7], ['10', 10], ['20', 20], ['50', 50],['100', 100]]
            }),
            valueField: 'value',
            displayField: 'text',
            emptyText: 10,
            width: 80,
            editable: false
        }],
        displayMsg: '显示{0}条数据到{1}条数据,共{2}条数据',
        plugins: 'ux-progressbarpager'
    }
});