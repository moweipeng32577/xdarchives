/**
 * Created by RonJiang on 2018/4/17 0017.
 */
Ext.define('Feedback.view.FeedbackAddFormView',{
    extend:'Ext.form.Panel',
    xtype:'feedbackAddFormView',
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
        allowBlank:false
    },{
        columnWidth: .02,
        xtype : 'displayfield',
        value: '<label style="color:#ff0b23;!important;">*</label>',
        margin:'41 25 1 3'
    },{
        columnWidth:.98,
        xtype : 'textarea',
        fieldLabel:'提问内容',
        name:'content',
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
        fieldLabel:'投件人',
        name:'askman',
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
        fieldLabel:'提问时间',
        name:'asktime',
        margin:'1 1 10 20',
        allowBlank:false
    },{
        columnWidth: .02,
        xtype : 'displayfield',
        value: '<label style="color:#ff0b23;!important;">*</label>',
        margin:'1 25 1 3'
    }
    // ,{
    //     columnWidth: 1,
    //     xtype: 'hidden',
    //     name: 'feedbackid'
    // }
    ],

    buttons:[{
        // text:'保存(Ctrl+S)',
        text:'保存',//增加反馈快捷键暂时未生效（有bug）
        itemId:'save'
    },'-',{
        text:'返回',
        itemId:'back'
    }]
});