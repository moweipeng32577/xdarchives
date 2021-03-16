/**
 * Created by Administrator on 2020/4/13.
 */


Ext.define('SelfPerformance.store.SelfPerformanceGridStore',{
    extend:'Ext.data.Store',
    model:'SelfPerformance.model.SelfPerformanceGridModel',
    pageSize: XD.pageSize,//此处设置每页显示记录条数
    remoteSort:true,
    proxy: {
        type: 'ajax',
        url:'/selfPerformance/getSelfPerformances',
        timeout:XD.timeout,
        reader: {
            type: 'json',
            rootProperty: 'content',
            totalProperty: 'totalElements'
        }
    }
});
