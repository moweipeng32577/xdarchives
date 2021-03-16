/**
 * Created by SunK on 2020/5/13 0013.
 */
Ext.define('ServiceMetadata.store.ServiceMetadataOperationStore',{
    extend:'Ext.data.Store',
    autoLoad: false,
    fields: ['configid','code'],
    proxy: {
        type: 'ajax',
        url: '/serviceMetadata/queryName',
        extraParams: {
            metadataType:'业务行为'
        },
        reader: {
            type: 'json'
        }
    }
});