/**
 * Created by xd on 2017/10/21.
 */
Ext.define('Restitution.model.RestitutionWghModel',{
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
        {name: 'jyts', type: 'string'},
        {name: 'jybackdate', type: 'string'},//审批通过时间
        {name: 'backdate', type: 'string'},//到期时间
        {name: 'renewreason', type: 'string'},//续借理由
        {name: 'approver', type: 'string'}//审批人
    ]
});