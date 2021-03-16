/**
 * Created by Administrator on 2020/9/28.
 */


Ext.define('SupervisionGuidance.model.SupervisionGuidanceOrganGridModel',{
    extend:'Ext.data.Model',
    fields: [
        {name: 'id', type: 'string'},
        {name: 'organname', type: 'string'},
        {name: 'classtype', type: 'string'},
        {name: 'underdepartment', type: 'string'},
        {name: 'username', type: 'string'},
        {name: 'username', type: 'string'},
        {name: 'post', type: 'string'},
        {name: 'politicstate', type: 'string'},
        {name: 'mobilephone', type: 'string'},
        {name: 'fulltimenum', type: 'int'},
        {name: 'parttimenum', type: 'int'},
        {name: 'organid', type: 'string'},
        {name: 'selectyear', type: 'string'}
    ]
});
