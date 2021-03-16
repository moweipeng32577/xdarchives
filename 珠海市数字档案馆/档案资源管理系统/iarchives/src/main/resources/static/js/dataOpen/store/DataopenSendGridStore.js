/**
 * Created by tanly on 2017/12/4 0004.
 */
Ext.define('Dataopen.store.DataopenSendGridStore',{
    extend:'Ext.data.Store',
    model:'Dataopen.model.DataopenDealGridModel',
    pageSize: XD.pageSize,
    //timeout:XD.timeout,
    proxy: {
        type: 'ajax',
        url: '/dataopen/getEntryIndexById',
        actionMethods:{read:'POST'},
        reader: {
            type: 'json',
            rootProperty: 'content',
            totalProperty: 'totalElements'
        }
    }
});