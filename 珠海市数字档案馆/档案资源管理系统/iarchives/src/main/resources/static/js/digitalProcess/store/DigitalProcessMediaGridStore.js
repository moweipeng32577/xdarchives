/**
 * Created by Administrator on 2019/9/20.
 */


Ext.define('DigitalProcess.store.DigitalProcessMediaGridStore',{
    extend:'Ext.data.Store',
    model:'DigitalProcess.model.DigitalProcessMediaGridModel',
    pageSize: XD.pageSize,
    remoteSort:true,
    groupField:'pagename',
    proxy: {
        type: 'ajax',
        url: '/digitalProcess/getSzhAuditEle',
        extraParams:{entryid:''},
        reader: {
            type: 'json',
            rootProperty: 'content',
            totalProperty: 'totalElements'
        }
    }
});
