/**
 * Created by Administrator on 2020/7/20.
 */
Ext.define('Affair.store.AffairDclStore',{
    extend:'Ext.data.Store',
    model:'Affair.model.AffairDclModel',
    pageSize: XD.pageSize,
    autoLoad: true,
    proxy: {
        type: 'ajax',
        url: '/projectRate/getProjectByAffair',
        extraParams: {projectstatus:'部门审核通过'},
        reader: {
            type: 'json',
            rootProperty: 'content',
            totalProperty: 'totalElements'
        }
    }
});