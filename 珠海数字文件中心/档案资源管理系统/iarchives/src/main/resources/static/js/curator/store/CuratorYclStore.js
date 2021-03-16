/**
 * Created by Administrator on 2020/7/21.
 */
Ext.define('Curator.store.CuratorYclStore',{
    extend:'Ext.data.Store',
    model:'Curator.model.CuratorYclModel',
    pageSize: XD.pageSize,
    autoLoad: true,
    proxy: {
        type: 'ajax',
        url: '/projectRate/getProjectByCurator',
        extraParams: {projectstatus:'领导审阅通过发布,领导审阅不发布'},
        reader: {
            type: 'json',
            rootProperty: 'content',
            totalProperty: 'totalElements'
        }
    }
});