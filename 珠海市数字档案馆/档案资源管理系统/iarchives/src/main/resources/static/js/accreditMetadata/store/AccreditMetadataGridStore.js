/**
 * Created by tanly on 2017/11/1 0024.
 */
Ext.define('AccreditMetadata.store.AccreditMetadataGridStore',{
    extend:'Ext.data.Store',
    model:'AccreditMetadata.model.AccreditMetadataGridModel',
    pageSize: XD.pageSize,
    remoteSort:true,
    proxy: {
        type: 'ajax',
        url: '/accreditMetadata/getByParentid',
        reader: {
            type: 'json',
            rootProperty: 'content',
            totalProperty: 'totalElements'
        }
    }
});