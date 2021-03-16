/**
 * Created by Rong on 2018/4/27.
 */
Ext.define('ReservoirArea.model.RoomColumnGridModel',{
    extend:'Ext.data.Model',
    fields: [
        {name: 'id', type: 'string',mapping:'nodeid'},
        {name: 'zonedisplay', type: 'string',mapping:'zone.zonedisplay'},
        {name: 'capacity', type: 'string',mapping:'capacity'},
        {name: 'usecapacity', type: 'string',mapping:'usecapacity'},
        {name: 'usage', type: 'string',mapping:'rate'}

    ]
});