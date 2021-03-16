/**
 * Created by Administrator on 2020/7/28.
 */


Ext.define('AuditOrder.store.ApproveOrganStore',{
    extend:'Ext.data.Store',
    fields: ['organid', 'organname'],
    proxy: {
        type: 'ajax',
        url: '/electron/getUnitOrganAll',
        extraParams: {},
        reader: {
            type: 'json',
            rootProperty: 'content',
            totalProperty: 'totalElements'
        }
    }
});
