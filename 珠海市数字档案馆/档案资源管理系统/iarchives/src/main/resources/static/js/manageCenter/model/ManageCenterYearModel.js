/**
 * Created by Administrator on 2020/7/21.
 */


Ext.define('ManageCenter.model.ManageCenterYearModel', {
    extend: 'Ext.data.Model',
    fields: [
        {name: 'year', type: 'string'},
        {name: 'elefile', type: 'string'},
        {name: 'elearchive', type: 'string'},
        {name: 'transfernum', type: 'string'}
    ]
});
