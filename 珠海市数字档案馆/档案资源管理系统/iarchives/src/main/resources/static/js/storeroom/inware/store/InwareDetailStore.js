/**
 * Created by Administrator on 2019/5/29.
 */
Ext.define('Inware.store.InwareDetailStore',{
    extend:'Ext.data.Store',
    pageSize: XD.pageSize,
    proxy: {
        type: 'ajax',
        url: '/management/addstorages',
        extraParams: {},
        reader: {
            type: 'json',
            rootProperty: 'content',
            totalProperty: 'totalElements'
        }
    }

});
