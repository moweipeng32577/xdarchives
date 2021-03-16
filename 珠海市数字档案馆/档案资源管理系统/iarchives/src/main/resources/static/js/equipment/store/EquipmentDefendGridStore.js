/**
 * Created by Administrator on 2020/6/24.
 */


Ext.define('Equipment.store.EquipmentDefendGridStore',{
    extend:'Ext.data.Store',
    model:'Equipment.model.EquipmentDefendGridModel',
    pageSize: XD.pageSize,
    remoteSort:true,
    proxy: {
        type: 'ajax',
        url: '/equipment/getEquipmentDefendByEquipmentId',
        reader: {
            type: 'json',
            rootProperty: 'content',
            totalProperty: 'totalElements'
        }
    }
});
