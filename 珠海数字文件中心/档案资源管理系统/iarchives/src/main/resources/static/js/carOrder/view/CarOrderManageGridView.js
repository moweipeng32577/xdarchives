/**
 * Created by Administrator on 2020/4/21.
 */

Ext.define('CarOrder.view.CarOrderManageGridView', {
    extend: 'Comps.view.BasicGridView',
    xtype: 'carOrderManageGridView',
    itemId: 'carOrderManageGridViewId',
    store: 'CarOrderManageGridStore',
    searchstore:[
        {item: 'carnumber', name: '车牌号码'},
        {item: 'cartype', name: '车型'},
        {item: 'state', name: '车辆状态'}
    ],
    columns: [
        {text: '车牌号码', dataIndex: 'carnumber', flex: 2, menuDisabled: true},
        {text: '车型', dataIndex: 'cartype', flex: 4, menuDisabled: true},
        {text: '车辆状态', dataIndex: 'state', flex: 2, menuDisabled: true},
        {text: '备注', dataIndex: 'remark', flex: 4, menuDisabled: true}
    ]
});
