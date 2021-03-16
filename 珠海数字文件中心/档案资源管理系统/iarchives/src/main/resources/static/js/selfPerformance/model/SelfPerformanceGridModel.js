/**
 * Created by Administrator on 2020/4/13.
 */


Ext.define('SelfPerformance.model.SelfPerformanceGridModel',{
    extend:'Ext.data.Model',
    fields: [
        {name: 'title', type: 'string'},
        {name: 'submitcount', type: 'int'},
        {name: 'successcount', type: 'int'},
        {name: 'failcount', type: 'int'}
    ]
});
