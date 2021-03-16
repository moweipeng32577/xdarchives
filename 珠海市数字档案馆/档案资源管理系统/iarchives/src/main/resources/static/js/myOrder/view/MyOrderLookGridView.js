/**
 * Created by Administrator on 2020/4/27.
 */

Ext.define('MyOrder.view.MyOrderLookGridView', {
    extend: 'Comps.view.BasicGridView',
    xtype:'myOrderLookGridView',
    hasSearchBar:false,
    hasCloseButton:false,
    hasPageBar:false,
    hasCheckColumn:false,
    region: 'south',
    height: '45%',
    store: 'MyOrderLookGridStore',
    columns: [
        {text: '审批环节', dataIndex: 'node', flex: 2, menuDisabled: true},
        {text: '审批人', dataIndex: 'spman', flex: 1, menuDisabled: true},
        {text: '审批状态', dataIndex: 'status', flex: 1, menuDisabled: true},
        {text: '时间', dataIndex: 'spdate', flex: 2, menuDisabled: true},
        {text: '批注', dataIndex: 'approve', flex: 4, menuDisabled: true}
    ]
});
