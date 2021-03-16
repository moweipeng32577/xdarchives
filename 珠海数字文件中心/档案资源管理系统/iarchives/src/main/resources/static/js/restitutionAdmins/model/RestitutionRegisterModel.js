/**
 * Created by xd on 2017/10/21.
 */
Ext.define('Restitution.model.RestitutionRegisterModel',{
    extend:'Ext.data.Model',
    fields: [
        {name: 'id', type: 'string'},
        {name: 'borrowman', type: 'string'},//查档人
        {name: 'borrowdate', type: 'string'},//查档日期
        {name: 'borrowmd', type: 'string'},//查档目的
        {name: 'borroworgan', type: 'string'},//查档机构
        {name: 'borrowts', type: 'string'},//查档天数
        {name: 'desci', type: 'string'},//查档描述
        {name: 'borrowdate', type: 'string'},//查档时间
        {name: 'borrowmantel', type: 'string'},//查档人电话
        {name: 'borrowcode', type: 'string'}//查档单号
    ]
});