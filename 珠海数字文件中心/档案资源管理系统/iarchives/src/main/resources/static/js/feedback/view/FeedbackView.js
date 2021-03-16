/**
 * Created by RonJiang on 2018/4/17 .
 */
Ext.define('Feedback.view.FeedbackView',{
    extend: 'Ext.panel.Panel',
    xtype:'feedback',
    layout:'card',
    activeItem:0,
    items:[{
        itemId:'gridview',
        xtype:'feedbackGridView'
    },{
        xtype:'feedbackAddFormView'
    },{
        xtype:'feedbackReplyFormView'
    },{
        xtype:'feedbackLookFormView'
    },{
        xtype:'appraiseGridView'
    }]
});