/**
 * Created by Administrator on 2020/4/17.
 */


Ext.define('CarManage.model.CarManageGridModel',{
    extend:'Ext.data.Model',
    fields: [
        {name: 'id', type: 'string'},
        {name: 'carnumber', type: 'string'},
        {name: 'cartype', type: 'string'},
        {name: 'state', type: 'string'},
        {name: 'remark', type: 'string'}
    ]
});
