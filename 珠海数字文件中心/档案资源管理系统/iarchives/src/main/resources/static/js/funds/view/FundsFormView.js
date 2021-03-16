/**
 * Created by RonJiang on 2018/04/08
 */
Ext.define('Funds.view.FundsFormView',{
    extend:'Ext.form.Panel',
    xtype:'fundsform',
    layout:'column',
    scrollable:true,
    defaults:{
        layout:'form',
        xtype:'textfield',
        labelWidth: 160,
        labelSeparator:'：'
    },
    items:[{
        columnWidth:.47,
        xtype : 'textfield',
        fieldLabel:'全宗名称',
        name:'fundsname',
        margin:'30 1 10 20',
        allowBlank:false
    },{
        columnWidth: .03,
        xtype : 'displayfield',
        value: '<label style="color:#ff0b23;!important;">*</label>',
        margin:'30 25 1 3'
    },{
        columnWidth:.47,
        xtype : 'textfield',
        itemId:'fundsItemid',
        fieldLabel:'全宗号',
        name:'funds',
        margin:'30 1 10 20',
        allowBlank:false
    },{
        columnWidth: .03,
        xtype : 'displayfield',
        value: '<label style="color:#ff0b23;!important;">*</label>',
        margin:'30 25 1 3'
    },{
        columnWidth:.47,
        xtype : 'textfield',
        fieldLabel:'全宗名称曾用名',
        name:'fundsnameformername',
        margin:'1 1 10 20',
        allowBlank:true
    },{
        columnWidth: .03,
        xtype : 'displayfield',
        margin:'1 25 1 3'
    },{
        columnWidth:.47,
        xtype : 'textfield',
        fieldLabel:'全宗指南文件',
        name:'fundsguidedoc',
        margin:'1 1 10 20',
        allowBlank:true
    },{
        columnWidth: .03,
        xtype : 'displayfield',
        margin:'1 25 1 3'
    },{
        columnWidth:.47,
        xtype : 'textfield',
        fieldLabel:'机构名称',
        name:'organname',
        margin:'1 1 10 20',
        allowBlank:true
    },{
        columnWidth: .03,
        xtype : 'displayfield',
        margin:'1 25 1 3'
    },{
        columnWidth:.47,
        xtype : 'textfield',
        fieldLabel:'机构成立时间',
        name:'organestablishtime',
        margin:'1 1 10 20',
        allowBlank:true
    },{
        columnWidth: .03,
        xtype : 'displayfield',
        margin:'1 25 1 3'
    },{
        columnWidth:.47,
        xtype : 'textfield',
        fieldLabel:'全宗起始时间',
        name:'fundsstarttime',
        margin:'1 1 10 20',
        allowBlank:true
    },{
        columnWidth: .03,
        xtype : 'displayfield',
        margin:'1 25 1 3'
    },{
        columnWidth:.47,
        xtype : 'textfield',
        fieldLabel:'全宗终止时间',
        name:'fundsendtime',
        margin:'1 1 10 20',
        allowBlank:true
    },{
        columnWidth: .03,
        xtype : 'displayfield',
        margin:'1 25 1 3'
    },{
        columnWidth:.47,
        xtype : 'textfield',
        fieldLabel:'归档文件总数',
        name:'filingtotalnum',
        margin:'1 1 10 20',
        allowBlank:true
    },{
        columnWidth: .03,
        xtype : 'displayfield',
        margin:'1 25 1 3'
    },{
    	columnWidth:.47,
        xtype : 'textfield',
        fieldLabel:'归档文书总数',
        name:'filingnum',
        margin:'1 0 10 20',
        allowBlank:true
    },{
        columnWidth: .03,
        xtype : 'displayfield',
        margin:'1 25 1 3'
    },{
        columnWidth:.47,
        xtype : 'textfield',
        fieldLabel:'归档短期（件）',
        name:'filingshortterm',
        margin:'1 1 10 20',
        allowBlank:true
    },{
        columnWidth: .03,
        xtype : 'displayfield',
        margin:'1 25 1 3'
    },{
        columnWidth:.47,
        xtype : 'textfield',
        fieldLabel:'归档长期（件）',
        name:'filinglongterm',
        margin:'1 1 10 20',
        allowBlank:true
    },{
        columnWidth: .03,
        xtype : 'displayfield',
        margin:'1 25 1 3'
    },{
        columnWidth:.47,
        xtype : 'textfield',
        fieldLabel:'归档永久（件）',
        name:'filingpermanent',
        margin:'1 1 10 20',
        allowBlank:true
    },{
        columnWidth: .03,
        xtype : 'displayfield',
        margin:'1 25 1 3'
    },{
        columnWidth:.47,
        xtype : 'textfield',
        fieldLabel:'案卷总数',
        name:'filetotalnum',
        margin:'1 1 10 20',
        allowBlank:true
    },{
        columnWidth: .03,
        xtype : 'displayfield',
        margin:'1 25 1 3'
    },{
        columnWidth:.47,
        xtype : 'textfield',
        fieldLabel:'卷内总份数',
        name:'jntotalcopies',
        margin:'1 1 10 20',
        allowBlank:true
    },{
        columnWidth: .03,
        xtype : 'displayfield',
        margin:'1 25 1 3'
    },{
        columnWidth:.47,
        xtype : 'textfield',
        fieldLabel:'文书案卷数',
        name:'wsfilenum',
        margin:'1 1 10 20',
        allowBlank:true
    },{
        columnWidth: .03,
        xtype : 'displayfield',
        margin:'1 25 1 3'
    },{
        columnWidth:.47,
        xtype : 'textfield',
        fieldLabel:'文书卷内份数',
        name:'wsjncopies',
        margin:'1 1 10 20',
        allowBlank:true
    },{
        columnWidth: .03,
        xtype : 'displayfield',
        margin:'1 25 1 3'
    },{
        columnWidth:.47,
        xtype : 'textfield',
        fieldLabel:'其他案卷数',
        name:'otherfilenum',
        margin:'1 1 10 20',
        allowBlank:true
    },{
        columnWidth: .03,
        xtype : 'displayfield',
        margin:'1 25 1 3'
    },{
        columnWidth:.47,
        xtype : 'textfield',
        fieldLabel:'其他卷内份数',
        name:'jnothercopies',
        margin:'1 1 10 20',
        allowBlank:true
    },{
        columnWidth: .03,
        xtype : 'displayfield',
        margin:'1 25 1 3'
    },{
        columnWidth:.47,
        xtype : 'textfield',
        fieldLabel:'文书短期（卷）',
        name:'wsshortterm',
        margin:'1 1 10 20',
        allowBlank:true
    },{
        columnWidth: .03,
        xtype : 'displayfield',
        margin:'1 25 1 3'
    },{
        columnWidth:.47,
        xtype : 'textfield',
        fieldLabel:'文书长期（卷）',
        name:'wslongterm',
        margin:'1 1 10 20',
        allowBlank:true
    },{
        columnWidth: .03,
        xtype : 'displayfield',
        margin:'1 25 1 3'
    },{
        columnWidth:.47,
        xtype : 'textfield',
        fieldLabel:'文书永久（卷）',
        name:'wspermanent',
        margin:'1 1 10 20',
        allowBlank:true
    },{
        columnWidth: .03,
        xtype : 'displayfield',
        margin:'1 25 1 3'
    },{
        columnWidth: 0.97,
        xtype : 'textarea',
        fieldLabel:'备注',
        name:'remarks',
        margin:'1 1 10 20',
        allowBlank:true
    },{
        columnWidth: .03,
        xtype : 'displayfield',
        margin:'1 25 1 3'
    },{
        columnWidth: 1,
        xtype: 'hidden',
        name: 'fundsid'
    }],

    buttons:[{
        xtype: "label",
        itemId:'tips',
        style:{color:'red'},
        text:'温馨提示：红色外框表示输入非法数据！',
        margin:'6 2 5 4'
    },{
        text:'保存(Ctrl+S)',
        itemId:'save'
    },'-',{
        text:'返回',
        itemId:'back'
    }]
});