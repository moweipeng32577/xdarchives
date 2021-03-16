/**
 * Created by SunK on 2018/10/11 0011.
 */
Ext.define('Appraisal.store.AppraisalGroupSetStore',{
    xtype:'appraisalGroupSetStore',
    extend:'Ext.data.Store',
    model:'Appraisal.model.AppraisalGroupSetModel',
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