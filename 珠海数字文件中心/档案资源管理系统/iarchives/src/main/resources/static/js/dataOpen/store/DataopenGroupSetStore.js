/**
 * Created by SunK on 2018/10/11 0011.
 */
Ext.define('Dataopen.store.DataopenGroupSetStore',{
    xtype:'dataopenGroupSetStore',
    extend:'Ext.data.Store',
    model:'Dataopen.model.DataopenGroupSetModel',
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
