/**
 * Created by tanly on 2017/11/1 0024.
 */
Ext.define('Organ.store.OrganGridStore', {
    extend: 'Ext.data.Store',
    model: 'Organ.model.OrganGridModel',
    pageSize: XD.pageSize,
    remoteSort:true,
    proxy: {
        type: 'ajax',
        url: '/organ/organs',
        timeout:XD.timeout,
        reader: {
            type: 'json',
            rootProperty: 'content',
            totalProperty: 'totalElements'
        }
    }
});