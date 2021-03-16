/**
 * Created by RonJiang on 2018/5/9 0009.
 */
Ext.define('AppraisalStandard.view.AppraisalStandardFormView',{
    extend:'Ext.form.Panel',
    xtype:'appraisalStandardFormView',
    layout:'column',
    defaults:{
        layout:'form',
        xtype:'textfield',
        labelWidth: 140,
        labelSeparator:'：'
    },
    items:[{
        columnWidth:.96,
        fieldLabel:'鉴定类型',
        name:'appraisaltypevalue',
        xtype: 'combobox',
        allowBlank: false,
        displayField: 'name',
        valueField: 'item',
        queryMode: 'local',
        store: Ext.create('Ext.data.Store', {
            proxy: {
                type: 'ajax',
                url: '/appraisalStandard/enums',
                reader: {
                    type: 'json'
                }
            },
            autoLoad: true
        }),
        margin:'30 1 10 15'
    },{
        columnWidth: .02,
        xtype: 'displayfield',
        value: '<label style="color:#ff0b23;!important;">*</label>',
        margin:'30 25 1 3'
    },{
        columnWidth:.96,
        fieldLabel:'鉴定标准值',
        name:'appraisalstandardvalue',
        margin:'15 1 10 15',
        allowBlank:false
    },{
        columnWidth: .02,
        xtype: 'displayfield',
        value: '<label style="color:#ff0b23;!important;">*</label>',
        margin:'15 25 1 3'
    },{
        columnWidth:.96,
        fieldLabel:'保管期限',
        name:'appraisalretention',
        xtype: 'combobox',
        editable: false,
        allowBlank: false,
        forceSelection: true,
        displayField: 'code',
        valueField: 'code',
        queryMode: 'local',
        store: Ext.create('Ext.data.Store', {
            proxy: {
                type: 'ajax',
                extraParams: {
                    value: 'Retention'
                },
                url: '/systemconfig/enums',
                reader: {
                    type: 'json'
                }
            },
            autoLoad: true
        }),
        margin:'15 1 10 15'
    },{
        columnWidth: .02,
        xtype: 'displayfield',
        value: '<label style="color:#ff0b23;!important;">*</label>',
        margin:'15 25 1 3'
    },{
        columnWidth:.96,
        fieldLabel:'鉴定标准值描述',
        name:'appraisaldesc',
        margin:'15 1 15 15'
    },{
        columnWidth: .02,
        xtype : 'displayfield'
    },{
        columnWidth: 1,
        xtype: 'hidden',
        name: 'appraisalstandardid'
    }],

    buttons:[{
        text:'保存',
        itemId:'save'
    },'-',{
        text:'返回',
        itemId:'back'
    }]
});