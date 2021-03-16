/**
 * Created by Administrator on 2019/9/12.
 */
Ext.define('SimpleSearchDirectory.store.ReportGridStore',{
    extend:'Ext.data.Store',
    model:'SimpleSearchDirectory.model.ReportGridModel',
    pageSize: XD.pageSize,
    remoteSort:true,
    proxy: {
        type: 'ajax',
        url: '/report/getNodeReport',
        extraParams: {},
        reader: {
            type: 'json',
            rootProperty: 'content',
            totalProperty: 'totalElements'
        }
    }
});
