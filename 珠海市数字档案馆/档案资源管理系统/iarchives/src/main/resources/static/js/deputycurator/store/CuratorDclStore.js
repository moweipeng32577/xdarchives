/**
 * Created by Administrator on 2020/7/20.
 */
Ext.define('Deputycurator.store.CuratorDclStore',{
    extend:'Ext.data.Store',
    model:'Deputycurator.model.CuratorDclModel',
    pageSize: XD.pageSize,
    autoLoad: true,
    proxy: {
        type: 'ajax',
        url: '/projectRate/getProjectByDeputyCurator',
        extraParams: {projectstatus:'部门审核通过,提交副馆长审阅'},
        reader: {
            type: 'json',
            rootProperty: 'content',
            totalProperty: 'totalElements'
        }
    }
});