/**
 * Created by Administrator on 2019/10/26.
 */


Ext.define('TransforAuditDeal.store.ElectronicVersionGridStore',{
    extend:'Ext.data.Store',
    model:'TransforAuditDeal.model.ElectronicVersionGridModel',
    pageSize: XD.pageSize,
    proxy: {
        type: 'ajax',
        url: '/acquisition/getEleVersion',
        extraParams: {
            eleid:""
        },
        reader: {
            type: 'json',
            rootProperty: 'content',
            totalProperty: 'totalElements'
        }
    }
});
