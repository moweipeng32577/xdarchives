/**
 * Created by Administrator on 2020/7/2.
 */


Ext.define('Template.store.MetadataGridStore',{
    extend:'Ext.data.Store',
    model:'Template.model.MetadataGridModel',
    pageSize: XD.pageSize,
    remoteSort:true,
    proxy: {
        type: 'ajax',
        url: '/template/templates',
        reader: {
            type: 'json',
            rootProperty: 'content',
            totalProperty: 'totalElements'
        }
    }
});
