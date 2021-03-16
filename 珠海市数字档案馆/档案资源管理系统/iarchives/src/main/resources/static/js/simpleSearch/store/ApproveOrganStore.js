/**
 * Created by Administrator on 2019/8/28.
 */


Ext.define('SimpleSearch.store.ApproveOrganStore',{
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
