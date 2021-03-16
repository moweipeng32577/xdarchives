/**
 * Created by Administrator on 2020/6/24.
 */


Ext.define('PlaceManage.model.PlaceDefendGridModel',{
    extend:'Ext.data.Model',
    fields: [
        {name: 'id', type: 'string'},
        {name: 'defendtype', type: 'string'},
        {name: 'defenduser', type: 'string'},
        {name: 'defendtime', type: 'string'},
        {name: 'defendcost', type: 'string'}
    ]
});
