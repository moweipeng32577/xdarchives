/**
 * Created by Administrator on 2020/4/17.
 */


Ext.define('CarManage.view.CarManageGridView', {
    extend: 'Comps.view.BasicGridView',
    xtype: 'carManageGridView',
    itemId: 'carManageGridViewId',
    store: 'CarManageGridStore',
    searchstore:[
        {item: 'carnumber', name: '车牌号码'},
        {item: 'cartype', name: '车型'},
        {item: 'state', name: '车辆状态'}
    ],
    tbar: [
        {
            itemId: 'addcar',
            xtype: 'button',
            text: '增加'
        },'-',{
            itemId: 'lookcar',
            xtype: 'button',
            text: '查看'
        },'-',{
            itemId: 'editcar',
            xtype: 'button',
            text: '修改'
        },'-',{
            itemId: 'deletecar',
            xtype: 'button',
            text: '删除'
        },'-',{
            itemId: 'defendcar',
            xtype: 'button',
            text: '维护记录'
        }
    ],
    columns: [
        {text: '车牌号码', dataIndex: 'carnumber', flex: 2, menuDisabled: true},
        {text: '车型', dataIndex: 'cartype', flex: 4, menuDisabled: true},
        {text: '车辆状态', dataIndex: 'state', flex: 2, menuDisabled: true},
        {text: '备注', dataIndex: 'remark', flex: 4, menuDisabled: true}
    ]
});
