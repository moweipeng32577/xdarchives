/**
 * Created by SunK on 2020/5/13 0013.
 */
Ext.define('ServiceMetadata.store.ServiceMetadataAccreditStore',{
    extend:'Ext.data.Store',
    autoLoad: false,
    fields: ['aid','shortname'],
    proxy: {
        type: 'ajax',
        url: '/serviceMetadata/queryAccreditName',
        extraParams: {
            metadataType:''
        },
        reader: {
            type: 'json'
        }
    }
});