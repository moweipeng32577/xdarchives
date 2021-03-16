/**
 * Created by yl on 2017/11/3.
 */
Ext.define('ExchangeStorage.view.ExchangeStorageDetailGridView', {
    extend: 'Ext.grid.Panel',
    xtype: 'exchangeStorageDetailGridView',
    region: 'east',
    width: '55%',
    requires: [
        'Ext.ux.ProgressBarPager'
    ],
    store: 'ExchangeStorageDetailGridStore',
    columnLines: true,
    viewConfig: {
        autoFill: true
    },
    split: true,
    style: 'text-align:left;',
    align: 'left',
    selModel: Ext.create('Ext.selection.CheckboxModel', {
        injectCheckbox: 0,//checkbox位于哪一列，默认值为0
        enableHdMenu: false,
        selType: "checkboxmodel"
    }),
    columns: [
        {xtype: "rownumberer", width:50, align: 'center'},
        {text: '题名', dataIndex: 'title', width:200, menuDisabled: true},
        {text: '全宗号', dataIndex: 'funds', width:200, menuDisabled: true},
        {text: '目录号', dataIndex: 'catalog', width:200, menuDisabled: true},
        {text: '文件编号', dataIndex: 'filenumber', width:200, menuDisabled: true},
        {text: '保管期限', dataIndex: 'entryretention', width:200, menuDisabled: true},
        {text: '文件日期', dataIndex: 'filedate', width:200, menuDisabled: true},
        {text: '归档年度', dataIndex: 'filingyear', width:200, menuDisabled: true},
        {text: 'f01', dataIndex: 'f01', width:200, menuDisabled: true},
        {text: 'f02', dataIndex: 'f02', width:200, menuDisabled: true}
    ],
    bbar: {
        xtype: 'pagingtoolbar',//底部工具栏是分页工具栏
        plugins: 'ux-progressbarpager',
        store: 'ExchangeStorageDetailGridStore',
        displayInfo: true,//显示共XX页，每页显示XX条的信息
        items: [{
            xtype: 'combobox',
            itemId: 'pagesizeComboID',
            name: 'pagesize',
            hiddenName: 'pagesize',
            store: new Ext.data.ArrayStore({
                fields: ['text', 'value'],
                data: [['5', 5], ['10', 10], ['20', 20], ['50', 50], ['100', 100], ['300', 300]]
            }),
            valueField: 'value',
            displayField: 'text',
            value:50,//使用默认分页大小
            width: 80,
            editable: false
        }]
    }
});