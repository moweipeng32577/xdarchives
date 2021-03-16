/**
 * Created by Administrator on 2020/7/21.
 */
Ext.define('Deputycurator.store.CuratorYclStore',{
    extend:'Ext.data.Store',
    model:'Deputycurator.model.CuratorYclModel',
    pageSize: XD.pageSize,
    autoLoad: true,
    proxy: {
        type: 'ajax',
        url: '/projectRate/getProjectByDeputyCurator',
        // extraParams: {projectstatus:'副领导审阅通过,副领导审阅不通过,领导审阅通过发布,领导审阅不发布'},
        extraParams: {projectstatus:'副领导审阅通过,副领导审阅不通过'},

        reader: {
            type: 'json',
            rootProperty: 'content',
            totalProperty: 'totalElements'
        }
    }
});