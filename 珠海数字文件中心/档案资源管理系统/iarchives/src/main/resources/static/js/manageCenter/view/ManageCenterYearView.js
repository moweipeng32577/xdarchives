/**
 * Created by Administrator on 2020/7/21.
 */


Ext.define('ManageCenter.view.ManageCenterYearView', {
    extend: 'Comps.view.BasicGridView',
    xtype: 'manageCenterYearView',
    itemId: 'manageCenterYearViewId',
    hasSearchBar: false,
    hasPageBar:false,
    hasCheckColumn:false,
    tbar: [],
    store: 'ManageCenterYearStore',
    columns: [
        {text: '年度', dataIndex: 'year', flex: 2, menuDisabled: true},
        {text: '电子文件', dataIndex: 'elefile', flex: 2, menuDisabled: true},
        {text: '电子档案', dataIndex: 'elearchive', flex: 2, menuDisabled: true},
        {text: '移交数量', dataIndex: 'transfernum', flex: 2, menuDisabled: true}
    ]
});
