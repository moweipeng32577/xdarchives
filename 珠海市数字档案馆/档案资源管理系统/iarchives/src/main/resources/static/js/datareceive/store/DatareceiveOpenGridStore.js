/**
 * Created by xd on 2017/10/21.
 */
Ext.define('Datareceive.store.DatareceiveOpenGridStore',{
    extend:'Ext.data.Store',
    model:'Datareceive.model.DatareceiveOpenGridModel',
    pageSize: XD.pageSize,
    remoteSort:true,
    proxy: {
        type: 'ajax',
        url: '/datareceive/getRelease',
        extraParams: {state:'待接收',type:'dataopen'},
        reader: {
            type: 'json',
            rootProperty: 'content',
            totalProperty: 'totalElements'
        }
    }
});
