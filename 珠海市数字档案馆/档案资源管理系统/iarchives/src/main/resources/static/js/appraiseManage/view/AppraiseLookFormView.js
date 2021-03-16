/**
 * Created by Administrator on 2020/3/23.
 */


Ext.define('AppraiseManage.view.AppraiseLookFormView',{
    extend:'Ext.form.Panel',
    xtype:'appraiseLookFormView',
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
        fieldLabel:'评分',
        name:'appraise',
        margin:'1 1 10 20',
        readOnly:true
    },{
        columnWidth: .02,
        xtype : 'displayfield',
        itemId:'appraisetextDisId'
    },{
        columnWidth:.98,
        xtype : 'textarea',
        fieldLabel:'评分内容',
        name:'appraisetext',
        margin:'1 1 10 20',
        readOnly:true
    },{
        columnWidth: .02,
        xtype : 'displayfield',
        itemId: "displayId"
    },{
        columnWidth:.48,
        xtype : 'textfield',
        fieldLabel:'投件人',
        name:'askman',
        margin:'1 1 10 20',
        readOnly:true
    },{
        columnWidth: .02,
        xtype : 'displayfield'
    },{
        columnWidth:.48,
        xtype : 'textfield',
        fieldLabel:'提问时间',
        name:'asktime',
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
        readOnly:true
    },{
        columnWidth: .02,
        xtype : 'displayfield'
    },{
        columnWidth:.48,
        xtype : 'textfield',
        fieldLabel:'提交人',
        name:'replyby',
        margin:'1 1 10 20',
        readOnly:true
    },{
        columnWidth: .02,
        xtype : 'displayfield'
    },{
        columnWidth:.48,
        xtype : 'textfield',
        fieldLabel:'提交时间',
        name:'replytime',
        margin:'1 1 10 20',
        readOnly:true
    },{
        columnWidth: .02,
        xtype : 'displayfield'
    }],
    buttons:[{
        text:'返回',
        itemId:'back'
    }]
});
