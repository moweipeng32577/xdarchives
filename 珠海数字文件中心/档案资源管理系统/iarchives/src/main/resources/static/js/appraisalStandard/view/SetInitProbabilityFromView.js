/**
 * Created by Leo on 2019/5/8 0008.
 */
Ext.define('AppraisalStandard.view.SetInitProbabilityFromView',{
    extend:'Ext.form.Panel',
    xtype:'setInitProbabilityFromView',
    layout:'column',
    defaults:{
        layout:'form',
        xtype:'textfield',
        labelWidth: 140,
        labelSeparator:'：'
    },
    items:[
        {
            columnWidth:.96,
            fieldLabel:'永久概率',
            name:'Y',
            margin:'15 1 10 15',
            allowBlank:false
        },
        {
        columnWidth: .02,
        xtype: 'displayfield',
        value: '<label style="color:#ff0b23;!important;">*</label>',
        margin:'15 25 1 3'
        },

        {
            columnWidth:.96,
            fieldLabel:'长期概率',
            name:'CQ',
            margin:'15 1 10 15',
            allowBlank:false
        },{
            columnWidth: .02,
            xtype: 'displayfield',
            value: '<label style="color:#ff0b23;!important;">*</label>',
            margin:'15 25 1 3'
        },

        {
            columnWidth:.96,
            fieldLabel:'短期概率',
            name:'DQ',
            margin:'15 1 10 15',
            allowBlank:false
        },
        {
            columnWidth: .02,
            xtype: 'displayfield',
            value: '<label style="color:#ff0b23;!important;">*</label>',
            margin:'15 25 1 3'
        }
    ],

    buttons:[{
        text:'保存',
        itemId:'save'
    },'-',{
        text:'返回',
        itemId:'back'
    }]
});