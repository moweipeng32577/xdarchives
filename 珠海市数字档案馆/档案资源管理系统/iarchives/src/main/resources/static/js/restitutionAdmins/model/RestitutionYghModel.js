/**
 * Created by xd on 2017/10/21.
 */
Ext.define('Restitution.model.RestitutionYghModel',{
    extend:'Ext.data.Model',
    fields: [
        {name: 'id', type: 'string'},
        {name: 'title', type: 'string'},
        {name: 'number', type: 'string'},
        {name: 'archivecode', type: 'string'},
        {name: 'funds', type: 'string'},
        {name: 'catalog', type: 'string'},
        {name: 'borrowman', type: 'string'},
        {name: 'borrowdate', type: 'string'},//查档时间
        {name: 'jybackdate', type: 'string'},//审批通过时间
        {name: 'approver', type: 'string'},//审批人
        {name: 'returnloginname', type: 'string'},//归还人账号
        {name: 'returnware', type: 'string'},//归还人
        {name: 'description', type: 'string'},//备注
    ]
});