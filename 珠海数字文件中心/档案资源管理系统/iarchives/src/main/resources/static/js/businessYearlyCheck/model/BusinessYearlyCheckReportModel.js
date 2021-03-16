/**
 * Created by Administrator on 2020/10/13.
 */


Ext.define('BusinessYearlyCheck.model.BusinessYearlyCheckReportModel',{
    extend:'Ext.data.Model',
    fields: [
        {name: 'id', type: 'string'},
        {name: 'selectyear', type: 'string'},
        {name: 'title', type: 'string'},
        {name: 'state', type: 'string'}
    ]
});
