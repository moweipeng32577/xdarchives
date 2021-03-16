/**
 * Created by Administrator on 2019/5/18.
 */


Ext.define('SimpleSearch.store.ApplyPrintEleGridStore',{
    extend:'Ext.data.Store',
    model:'SimpleSearch.model.ApplyPrintEleGridModel',
    pageSize: XD.pageSize,//此处设置每页显示记录条数
    remoteSort:true,
    proxy: {
        type: 'ajax',
        url:'/simpleSearch/getApplySetPrint',
        extraParams: {
            entryid:''
        },
        reader: {
            type: 'json',
            rootProperty: 'content',
            totalProperty: 'totalElements'
        }
    },
    listeners: {
        load:function(st, rds, opts) {
            if(st.data.length==0)
            XD.msg("文件不存在！")
        },
    }
});
