/**
 * Created by RonJiang on 2018/04/17.
 */
Ext.define('Feedback.model.FeedbackGridModel',{
    extend:'Ext.data.Model',
    fields: [
        {name: 'id', type: 'string',mapping:'feedbackid'},
        {name: 'title', type: 'string'},
        {name: 'askman', type: 'string'},
        {name: 'asktime', type: 'string'},
        {name: 'flag', type: 'string'}
    ]
});