/**
 * Created by Administrator on 2020/7/20.
 */
Ext.define('ProjectAdd.store.AddDclStore',{
    extend:'Ext.data.Store',
    model:'ProjectAdd.model.AddDclModel',
    pageSize: XD.pageSize,
    autoLoad: true,
    proxy: {
        type: 'ajax',
        url: '/projectRate/getProjectManages',
        // extraParams: {projectstatus:'新增项目,部门审核不通过,副领导审阅不通过,领导审阅不发布'},
        extraParams: {projectstatus:'新增项目'},
        reader: {
            type: 'json',
            rootProperty: 'content',
            totalProperty: 'totalElements'
        }
    }
});