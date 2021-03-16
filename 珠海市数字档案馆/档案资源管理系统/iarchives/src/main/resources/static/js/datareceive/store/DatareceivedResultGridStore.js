/**
 * Created by yl on 2020/6/28.
 */
Ext.define('Datareceive.store.DatareceivedResultGridStore',{
    extend:'Ext.data.Store',
    model:'Datareceive.model.DatareceivedResultGridModel',
    pageSize: XD.pageSize,
    remoteSort:true,
    proxy: {
        type: 'ajax',
        url: '/datareceive/getImplementResult',
        extraParams: {},
        reader: {
            type: 'json',
            rootProperty: 'content',
            totalProperty: 'totalElements'
        }
    }
});