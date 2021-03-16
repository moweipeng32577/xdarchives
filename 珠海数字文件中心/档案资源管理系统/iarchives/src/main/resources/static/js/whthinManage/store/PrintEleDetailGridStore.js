/**
 * Created by Administrator on 2019/5/28.
 */

Ext.define('WhthinManage.store.PrintEleDetailGridStore',{
    extend:'Ext.data.Store',
    model:'WhthinManage.model.PrintEleDetailGridModel',
    pageSize: XD.pageSize,//此处设置每页显示记录条数
    remoteSort:true,
    proxy: {
        type: 'ajax',
        url:'/electronPrintApprove/getApproveSetPrint',
        extraParams: {
            entryid:'',
            borrowcode:'',
            type:''
        },
        reader: {
            type: 'json',
            rootProperty: 'content',
            totalProperty: 'totalElements'
        }
    }
});
