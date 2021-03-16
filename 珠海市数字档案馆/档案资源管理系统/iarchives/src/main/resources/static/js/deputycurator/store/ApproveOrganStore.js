/**
 * Created by Administrator on 2019/8/29.
 */


Ext.define('Deputycurator.store.ApproveOrganStore',{
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
