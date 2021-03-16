/**
 * Created by Administrator on 2019/9/20.
 */


Ext.define('DigitalProcess.store.DigitalProcessAuditAuditGridStore',{
    extend:'Ext.data.Store',
    model:'DigitalProcess.model.DigitalProcessAuditAuditGridModel',
    pageSize:1000,
    proxy: {
        type: 'ajax',
        url: '/digitalProcess/getcalloutEntrys',
        extraParams:{ids:null},
        reader: {
            type: 'json',
            rootProperty: 'content',
            totalProperty: 'totalElements'
        }
    }
});
