/**
 * Created by Administrator on 2020/4/23.
 */


Ext.define('PlaceOrder.view.PlaceOrderLookGridView', {
    extend: 'Comps.view.BasicGridView',
    xtype:'placeOrderLookGridView',
    hasSearchBar:false,
    hasCloseButton:false,
    hasPageBar:false,
    hasCheckColumn:false,
    region: 'south',
    height: '45%',
    store: 'PlaceOrderLookGridStore',
    columns: [
        {text: '审批环节', dataIndex: 'node', flex: 2, menuDisabled: true},
        {text: '审批人', dataIndex: 'spman', flex: 1, menuDisabled: true},
        {text: '审批状态', dataIndex: 'status', flex: 1, menuDisabled: true},
        {text: '时间', dataIndex: 'spdate', flex: 2, menuDisabled: true},
        {text: '批注', dataIndex: 'approve', flex: 4, menuDisabled: true}
    ]
});