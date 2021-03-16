/**
 * Created by Administrator on 2020/9/28.
 */


Ext.define('SupervisionGuidance.model.SupervisionGuidanceFileUserGridModel',{
    extend:'Ext.data.Model',
    fields: [
        {name: 'id', type: 'string'},
        {name: 'username', type: 'string'},
        {name: 'sex', type: 'string'},
        {name: 'post', type: 'string'},
        {name: 'officephone', type: 'string'},
        {name: 'mobilephone', type: 'string'},
        {name: 'isfulltime', type: 'string'},
        {name: 'workdate', type: 'string'},
        {name: 'aduitdate', type: 'string'}

    ]
});

