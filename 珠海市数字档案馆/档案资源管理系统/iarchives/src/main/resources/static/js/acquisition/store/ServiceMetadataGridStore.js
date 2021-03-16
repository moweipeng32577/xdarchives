/**
 * Created by SunK on 2020/6/23 0023.
 */
Ext.define('Acquisition.store.ServiceMetadataGridStore',{
    extend:'Ext.data.Store',
    model:'Acquisition.model.ServiceMetadataGridModel',
    pageSize: XD.pageSize,
    proxy: {
        type: 'ajax',
        url: '/metadataManagement/getServiceMetadataByEntryid',
        extraParams: {},
        reader: {
            type: 'json',
            rootProperty: 'content',
            totalProperty: 'totalElements'
        }
    }
});
