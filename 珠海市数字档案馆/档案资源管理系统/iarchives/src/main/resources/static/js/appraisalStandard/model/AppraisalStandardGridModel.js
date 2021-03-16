/**
 * Created by RonJiang on 2018/5/9 0009.
 */
Ext.define('AppraisalStandard.model.AppraisalStandardGridModel',{
    extend:'Ext.data.Model',
    fields: [
        {name: 'id', type: 'string',mapping:'appraisalstandardid'},
        {name: 'appraisaltypevalue', type: 'string'},
        {name: 'appraisalstandardvalue', type: 'string'},
        {name: 'appraisalretention', type: 'string'},
        {name: 'appraisaldesc', type: 'string'}
    ]
});