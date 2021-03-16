/**
 * Created by Administrator on 2018/9/12.
 */

Ext.define('Organ.store.OrganSequenceStore', {
    extend: 'Ext.data.Store',
    model: 'Organ.model.OrganGridModel',
    pageSize: XD.pageSize,
    remoteSort:true,
    proxy: {
        type: 'ajax',
        url: '/organ/organids',
        reader: {
            type: 'json',
            rootProperty: 'content',
            totalProperty: 'totalElements'
        }
    }
});
