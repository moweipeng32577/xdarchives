/**
 * Created by SunK on 2020/6/10 0010.
 */
Ext.define('MetadataManagement.store.AddMetadataOperationStore',{
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