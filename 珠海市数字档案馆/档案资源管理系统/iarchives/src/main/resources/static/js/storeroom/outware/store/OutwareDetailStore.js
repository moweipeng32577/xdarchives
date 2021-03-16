/**
 * Created by Administrator on 2019/5/29.
 */
Ext.define('Outware.store.OutwareDetailStore',{
    extend:'Ext.data.Store',
    pageSize: XD.pageSize,
    proxy: {
        type: 'ajax',
        url: '/management/addOutwares',
        extraParams: {},
        reader: {
            type: 'json',
            rootProperty: 'content',
            totalProperty: 'totalElements'
        }
    }

});
