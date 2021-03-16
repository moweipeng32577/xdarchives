/**
 * Created by SunK on 2018/10/11 0011.
 */
Ext.define('Acquisition.store.AcquisitionGroupSetStore',{
    xtype:'acquisitionGroupSetStore',
    extend:'Ext.data.Store',
    model:'Acquisition.model.AcquisitionGroupSetModel',
    //autoLoad:true,
    proxy:({
        type: 'ajax',
        url: '/export/managementGetFields',
        extraParams:{fieldNodeid:0},
        reader: {
            type: 'json'
        }
    }),
    //idProperty: 'fieldname',
    fields: ['fieldcode','fieldname']
});