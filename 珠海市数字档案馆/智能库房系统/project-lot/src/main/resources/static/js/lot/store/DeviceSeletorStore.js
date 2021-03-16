/**
 * Created by Rong on 2019-01-17.
 */
Ext.define('Lot.store.DeviceSeletorStore',{
    extend:'Ext.data.Store',
    autoLoad:true,
    model:'Lot.model.DeviceSeletorModel',
    idProperty: 'id',
    fields: ['id','name'],
    proxy: {
        type: 'ajax',
        url: '/seletorDevice',
        reader: {
            type: 'json'
        }
    }
});