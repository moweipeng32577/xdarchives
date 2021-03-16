/**
 * Created by RonJiang on 2018/4/17 0017.
 */
Ext.define('Feedback.view.FeedbackReplyFormView',{
    extend:'Ext.form.Panel',
    xtype:'feedbackReplyFormView',
    layout:'column',
    defaults:{
        layout:'form',
        xtype:'textfield',
        labelWidth: 80,
        labelSeparator:'：'
    },
    items:[{
        columnWidth:.98,
        xtype : 'textfield',
        fieldLabel:'标题',
        name:'title',
        margin:'30 1 10 20',
        readOnly:true
    },{
        columnWidth: .02,
        xtype : 'displayfield'
    },{
        columnWidth:.98,
        xtype : 'textarea',
        fieldLabel:'提问内容',
        name:'content',
        margin:'1 1 10 20',
        readOnly:true
    },{
        columnWidth: .02,
        xtype : 'displayfield'
    },{
        columnWidth:.98,
        xtype : 'textfield',
        fieldLabel:'回复内容',
        name:'replycontent',
        margin:'1 1 10 20',
        allowBlank:false
    },{
        columnWidth: .02,
        xtype : 'displayfield',
        value: '<label style="color:#ff0b23;!important;">*</label>',
        margin:'1 25 1 3'
    },{
        columnWidth:.48,
        xtype : 'textfield',
        fieldLabel:'提交人',
        name:'replyby',
        margin:'1 1 10 20',
        allowBlank:false
    },{
        columnWidth: .02,
        xtype : 'displayfield',
        value: '<label style="color:#ff0b23;!important;">*</label>',
        margin:'1 25 1 3'
    },{
        columnWidth:.48,
        xtype : 'textfield',
        fieldLabel:'提交时间',
        name:'replytime',
        margin:'1 1 10 20',
        allowBlank:false
    },{
        columnWidth: .02,
        xtype : 'displayfield',
        value: '<label style="color:#ff0b23;!important;">*</label>',
        margin:'1 25 1 3'
    },{
        columnWidth: 1,
        xtype: 'hidden',
        name: 'feedbackid'
    },{
        columnWidth:1,
        xtype : 'hidden',
        fieldLabel:'投件人',
        name:'askman',
        margin:'1 1 10 20',
        readOnly:true
    },{
        columnWidth:1,
        xtype : 'hidden',
        fieldLabel:'提问时间',
        name:'asktime',
        margin:'1 1 10 20',
        readOnly:true
    }],

    buttons:[{
        text:'保存(Ctrl+S)',
        itemId:'save'
    },'-',{
        text:'返回',
        itemId:'back'
    }]
});