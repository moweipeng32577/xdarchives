/**
 * Created by Administrator on 2020/7/20.
 */
Ext.define('Curator.store.CuratorDclStore',{
    extend:'Ext.data.Store',
    model:'Curator.model.CuratorDclModel',
    pageSize: XD.pageSize,
    autoLoad: true,
    proxy: {
        type: 'ajax',
        url: '/projectRate/getProjectByCurator',
        extraParams: {projectstatus:'副领导审阅通过,提交馆长审阅'},
        reader: {
            type: 'json',
            rootProperty: 'content',
            totalProperty: 'totalElements'
        }
    }
});