/**
 * Created by Administrator on 2020/7/21.
 */
Ext.define('Affair.store.AffairYclStore',{
    extend:'Ext.data.Store',
    model:'Affair.model.AffairYclModel',
    pageSize: XD.pageSize,
    autoLoad: true,
    proxy: {
        type: 'ajax',
        url: '/projectRate/getProjectByAffair',
        extraParams: {projectstatus:'提交副领导审阅,副领导审阅通过,副领导审阅不通过,领导审阅通过发布,领导审阅不发布'},
        reader: {
            type: 'json',
            rootProperty: 'content',
            totalProperty: 'totalElements'
        }
    }
});