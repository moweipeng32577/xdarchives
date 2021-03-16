/**
 * Created by yl on 2020/3/20.
 */
Ext.define('Datareceive.store.DatareceiveThematicGridStore',{
    extend:'Ext.data.Store',
    model:'Datareceive.model.DatareceiveOpenGridModel',
    pageSize: XD.pageSize,
    remoteSort:true,
    proxy: {
        type: 'ajax',
        url: '/datareceive/getRelease',
        extraParams: {state:'待接收',type:'thematic'},
        reader: {
            type: 'json',
            rootProperty: 'content',
            totalProperty: 'totalElements'
        }
    }
});