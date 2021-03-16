/**
 * Created by RonJiang on 2018/4/20 0020.
 */
Ext.define('Appraisal.view.AppraisalBillView', {
    extend:'Ext.panel.Panel',
    xtype:'appraisalBillView',
    layout:'card',
    activeItem:0,
    items:[{
        xtype:'appraisalShowBillGridView'
    },{
        xtype:'appraisalBillEntryGridView'
    }]
});