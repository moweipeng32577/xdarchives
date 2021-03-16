/**
 * Created by Administrator on 2020/9/28.
 */



Ext.define('SupervisionGuidance.model.SupervisionGuidanceWorkFundsGridModel',{
    extend:'Ext.data.Model',
    fields: [
        {name: 'id', type: 'string'},
        {name: 'selectyear', type: 'string'},
        {name: 'archivesfunds', type: 'string'},
        {name: 'situatuion', type: 'string'},
        {name: 'remark', type: 'string'}
    ]
});