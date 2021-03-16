/**
 * Created by Administrator on 2020/9/28.
 */


Ext.define('SupervisionGuidance.model.SupervisionGuidanceWorkPlanGridModel',{
    extend:'Ext.data.Model',
    fields: [
        {name: 'id', type: 'string'},
        {name: 'selectyear', type: 'string'},
        {name: 'isyearplan', type: 'string'},
        {name: 'isyearconclusion', type: 'string'},
        {name: 'isyearaduit', type: 'string'},
        {name: 'attachment', type: 'string'}
    ]
});

