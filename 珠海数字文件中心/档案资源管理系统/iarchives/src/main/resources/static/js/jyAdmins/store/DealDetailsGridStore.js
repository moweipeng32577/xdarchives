/**
 * Created by Administrator on 2018/10/22.
 */

Ext.define('JyAdmins.store.DealDetailsGridStore',{
    extend:'Ext.data.Store',
    model:'JyAdmins.model.DealDetailsGridModel',
    pageSize: XD.pageSize,
    proxy: {
        type: 'ajax',
        url: '/jyAdmins/getDealDetails',
        extraParams: {},
        reader: {
            type: 'json',
            rootProperty: 'content',
            totalProperty: 'totalElements'
        }
    }
});
