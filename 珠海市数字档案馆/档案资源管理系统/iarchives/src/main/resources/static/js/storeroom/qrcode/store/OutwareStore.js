/**
 * Created by zengdw on 2018/05/09 0001.
 */
Ext.define('Qrcode.store.OutwareStore',{
    extend:'Ext.data.Store',
    model:'Qrcode.model.OutwareModel',
    pageSize: XD.pageSize,
    proxy: {
        type: 'ajax',
        url: '/outware/outwares',
        reader: {
            type: 'json',
            rootProperty: 'content',
            totalProperty: 'totalElements'
        }
    }
});