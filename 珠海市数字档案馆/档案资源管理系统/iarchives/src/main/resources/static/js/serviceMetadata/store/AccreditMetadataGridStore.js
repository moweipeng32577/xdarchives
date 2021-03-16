/**
 * Created by tanly on 2017/11/1 0024.
 */
Ext.define('ServiceMetadata.store.AccreditMetadataGridStore',{
    extend:'Ext.data.Store',
    model:'ServiceMetadata.model.AccreditMetadataGridModel',
    pageSize: XD.pageSize,
    remoteSort:true,
    proxy: {
        type: 'ajax',
        url: '/serviceMetadata/getByParentid',
        reader: {
            type: 'json',
            rootProperty: 'content',
            totalProperty: 'totalElements'
        }
    }
});