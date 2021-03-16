/**
 * Created by Administrator on 2019/5/27.
 */


Ext.define('JyAdmins.store.DzPrintGridStore',{
    extend:'Ext.data.Store',
    model:'JyAdmins.model.DzJyGridModel',
    pageSize: XD.pageSize,
    remoteSort:true,
    proxy: {
        type: 'ajax',
        url: '/jyAdmins/getBorrowdoc',
        extraParams: {state:'',type:'',flag:iflag},
        reader: {
            type: 'json',
            rootProperty: 'content',
            totalProperty: 'totalElements'
        }
    }
});
