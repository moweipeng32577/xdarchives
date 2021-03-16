/**
 * Created by SunK on 2020/6/10 0010.
 */
Ext.define('MetadataManagement.store.AddMetadataAccreditStore',{
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