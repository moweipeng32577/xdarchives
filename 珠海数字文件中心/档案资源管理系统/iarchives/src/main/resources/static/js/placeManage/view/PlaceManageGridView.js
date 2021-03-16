/**
 * Created by Administrator on 2020/4/20.
 */


Ext.define('PlaceManage.view.PlaceManageGridView', {
    extend: 'Comps.view.BasicGridView',
    xtype: 'placeManageGridView',
    itemId: 'placeManageGridViewId',
    store: 'PlaceManageGridStore',
    searchstore:[
        {item: 'floor', name: '楼层'},
        {item: 'placedesc', name: '场地描述'},
        {item: 'state', name: '场地状态'}
    ],
    tbar: [
        {
            itemId: 'addplace',
            xtype: 'button',
            text: '增加'
        },'-',{
            itemId: 'lookplace',
            xtype: 'button',
            text: '查看'
        },'-',{
            itemId: 'editplace',
            xtype: 'button',
            text: '修改'
        },'-',{
            itemId: 'deleteplace',
            xtype: 'button',
            text: '删除'
        },'-',{
            itemId: 'defendcar',
            xtype: 'button',
            text: '维护记录'
        }
    ],
    columns: [
        {text: '楼层', dataIndex: 'floor', flex: 2, menuDisabled: true},
        {text: '场地描述', dataIndex: 'placedesc', flex: 4, menuDisabled: true},
        {text: '场地状态', dataIndex: 'state', flex: 2, menuDisabled: true},
        {text: '备注', dataIndex: 'remark', flex: 4, menuDisabled: true}
    ]
});
