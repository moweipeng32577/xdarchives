/**
 * Created by SunK on 2020/5/6 0006.
 */
Ext.define('MetadataSearch.store.MetadataSearchTypeStore',{
    extend:'Ext.data.Store',
    autoLoad: false,
    fields: ['templateid','fieldname'],
    proxy: {
        type: 'ajax',
        url: '/metadataSearch/queryName',
        extraParams: {
            metadataType:''
        },
        reader: {
            type: 'json'
        }
    }
});