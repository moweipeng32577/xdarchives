/**
 * Created by xd on 2017/10/21.
 */
Ext.define('JyAdmins.store.DzJyGridStore',{
    extend:'Ext.data.Store',
    model:'JyAdmins.model.DzJyGridModel',
    pageSize: XD.pageSize,
    remoteSort:true,
    proxy: {
        type: 'ajax',
        url: '/jyAdmins/getBorrowdoc',
        extraParams: {state:'待处理',type:'查档',flag:iflag},
        reader: {
            type: 'json',
            rootProperty: 'content',
            totalProperty: 'totalElements'
        }
    }
});
