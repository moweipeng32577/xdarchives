/**
 * Created by Administrator on 2020/6/24.
 */


Ext.define('Equipment.view.EquipmentDefendGridView', {
    extend: 'Comps.view.BasicGridView',
    xtype: 'equipmentDefendGridView',
    itemId: 'equipmentDefendGridViewId',
    store: 'EquipmentDefendGridStore',
    searchstore:[
        {item: 'defendtype', name: '维护类型'},
        {item: 'defenduser', name: '登记人'},
        {item: 'defendtime', name: '维护时间'}
    ],
    tbar: [
        {
            itemId: 'addDefend',
            xtype: 'button',
            text: '增加'
        },'-',{
            itemId: 'lookDefend',
            xtype: 'button',
            text: '查看'
        },'-',{
            itemId: 'editDefend',
            xtype: 'button',
            text: '修改'
        },'-',{
            itemId: 'deleteDefend',
            xtype: 'button',
            text: '删除'
        },'-',{
            itemId: 'back',
            xtype: 'button',
            text: '返回'
        }
    ],
    columns: [
        {text: '维护类型', dataIndex: 'defendtype', flex: 2, menuDisabled: true},
        {text: '登记人', dataIndex: 'defenduser', flex: 4, menuDisabled: true},
        {text: '维护时间', dataIndex: 'defendtime', flex: 4, menuDisabled: true},
        {text: '维护费用', dataIndex: 'defendcost', flex: 4, menuDisabled: true}
    ]
});
