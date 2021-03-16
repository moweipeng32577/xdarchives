/**
 * Created by Leo on 2019/04/25.
 */
Ext.define('FileChecker.view.FileCheckerView',{
    extend:'Comps.view.BasicGridView',
    xtype:'fileCheckerView',
    id:'fileCheckerView',
    itemId:'fileCheckerViewID',

    isChecking:0,
    stripeRows: true,
    hasSearchBar:false,
    hasCheckColumn:false,
    allowDrag:false,
    // allowSelect:false,
    tbar: functionButton,
    store: 'FileCheckerStore',
    columns: [
        {text: '文件名', dataIndex: 'filename', flex: 2, menuDisabled: true},
        {text: '文件路径', dataIndex: 'filepath', flex: 2, menuDisabled: true},
        {text: 'MD5码', dataIndex: 'md5', flex: 2, menuDisabled: true},
        {text: '页数', dataIndex: 'pages', flex: 2, menuDisabled: true},
        {text: '固化结果', dataIndex: 'solid', flex: 2, menuDisabled: true},
        {text: '巡查结果', dataIndex: 'resultText', flex: 2, menuDisabled: true},
        {text: '最后巡查时间', dataIndex: 'lastCheckTime', flex: 2, menuDisabled: true}
    ],
    listeners: {
        // cellclick: function (grid, rowIndex, columnIndex, event) {
        //     console.log(grid.getRow(rowIndex));
        //     console.log(columnIndex);
        // }
    }
});