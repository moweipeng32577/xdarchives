/**
 * Created by Administrator on 2020/10/15.
 */


Ext.define('BusinessYearlyCheck.model.BusinessYearlyCheckSubmitGridModel',{
    extend:'Ext.data.Model',
    fields: [
        {name: 'id', type: 'string'},
        {name: 'selectyear', type: 'string'},
        {name: 'title', type: 'string'}
    ]
});
