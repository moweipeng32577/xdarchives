/**
 * Created by Administrator on 2020/7/21.
 */


Ext.define('ManageCenter.view.ManageCenterUnitView', {
    extend: 'Comps.view.BasicGridView',
    xtype: 'manageCenterUnitView',
    itemId: 'manageCenterUnitViewId',
    hasSearchBar: false,
    hasCheckColumn:false,
    tbar: [],
    store: 'ManageCenterUnitStore',
    columns: [
        {text: '立档单位', dataIndex: 'unit', flex: 2, menuDisabled: true},
        {text: '电子文件', dataIndex: 'elefile', flex: 2, menuDisabled: true},
        {text: '电子档案', dataIndex: 'elearchive', flex: 2, menuDisabled: true},
        {text: '移交数量', dataIndex: 'transfernum', flex: 2, menuDisabled: true}
    ]
});
