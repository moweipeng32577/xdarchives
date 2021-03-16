/**
 * Created by Administrator on 2020/7/21.
 */
Ext.define('ProjectAdd.store.AddYclStore',{
    extend:'Ext.data.Store',
    model:'ProjectAdd.model.AddYclModel',
    pageSize: XD.pageSize,
    autoLoad: true,
    proxy: {
        type: 'ajax',
        url: '/projectRate/getProjectManages',
        extraParams: {projectstatus:'提交部门审核,部门审核通过,部门审核不通过,提交副馆长审阅,副领导审阅通过,副领导审阅不通过,领导审阅通过发布,领导审阅不发布'},
        reader: {
            type: 'json',
            rootProperty: 'content',
            totalProperty: 'totalElements'
        }
    }
});