/**
 * Created by Administrator on 2020/7/9.
 */


Ext.define('SupervisionGuidance.model.SupervisionGuidanceLeaderGridModel',{
    extend:'Ext.data.Model',
    fields: [
        {name: 'id', type: 'string'},
        {name: 'username', type: 'string'},
        {name: 'post', type: 'string'},
        {name: 'politicstate', type: 'string'},
        {name: 'starttime', type: 'string'},
        {name: 'mobilephone', type: 'string'},
        {name: 'organid', type: 'string'},
        {name: 'selectyear', type: 'string'}
    ]
});
