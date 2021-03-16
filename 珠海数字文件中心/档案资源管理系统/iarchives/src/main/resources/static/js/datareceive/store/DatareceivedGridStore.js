/**
 * Created by yl on 2020/3/18.
 */
Ext.define('Datareceive.store.DatareceivedGridStore',{
    extend:'Ext.data.Store',
    model:'Datareceive.model.DatareceiveOpenGridModel',
    pageSize: XD.pageSize,
    remoteSort:true,
    proxy: {
        type: 'ajax',
        url: '/datareceive/getRelease',
        extraParams: {state:'已接收',type:'dataopen'},
        reader: {
            type: 'json',
            rootProperty: 'content',
            totalProperty: 'totalElements'
        }
    }
});