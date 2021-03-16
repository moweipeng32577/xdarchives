/**
 * Created by Administrator on 2020/4/28.
 */


Ext.define('PlaceOrder.view.PlaceOrderManageGridView', {
    extend: 'Comps.view.BasicGridView',
    xtype: 'placeOrderManageGridView',
    itemId: 'placeOrderManageGridViewId',
    store: 'PlaceOrderManageGridStore',
    searchstore:[
        {item: 'floor', name: '楼层'},
        {item: 'placedesc', name: '场地描述'},
        {item: 'state', name: '场地状态'}
    ],
    columns: [
        {text: '楼层', dataIndex: 'floor', flex: 2, menuDisabled: true},
        {text: '场地描述', dataIndex: 'placedesc', flex: 4, menuDisabled: true},
        {text: '场地状态', dataIndex: 'state', flex: 2, menuDisabled: true},
        {text: '备注', dataIndex: 'remark', flex: 4, menuDisabled: true}
    ]
});
