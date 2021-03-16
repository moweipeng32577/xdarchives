/**
 * Created by Administrator on 2020/6/22.
 */



Ext.define('ElectronPrintApprove.store.ApproveOrganStore',{
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
