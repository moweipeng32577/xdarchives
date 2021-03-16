/**
 * Created by tanly on 2017/12/2 0002.
 */
Ext.define('Dataopen.store.DataopenDealGridStore',{
    extend:'Ext.data.Store',
    model:'Dataopen.model.DataopenDealGridModel',
    pageSize: XD.pageSize,
    proxy: {
        type: 'ajax',
        url: '/dataopen/getBoxEntryIndex',
        timeout:XD.timeout,
        reader: {
            type: 'json',
            rootProperty: 'content',
            totalProperty: 'totalElements'
        }
    }
});