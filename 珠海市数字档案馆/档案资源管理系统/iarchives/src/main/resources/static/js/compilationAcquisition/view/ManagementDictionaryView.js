/**
 * 分类字典视图
 */
Ext.define('CompilationAcquisition.view.ManagementDictionaryView',{
    extend:'Ext.form.Panel',
    xtype:'managementDictionaryView',
    layout:'column',
    defaults:{
        layout:'form',
        xtype:'textfield',
        labelWidth: 100,
        labelSeparator:'：'
    },
    items:[{
        columnWidth:.4,
        fieldLabel:'年度',
        name:'filingyear',
        xtype: 'combobox',
        queryMode: 'local',
        displayField:'year',
        valueField:'year',
        editable: false,
        store: {
            type:'array',
            fields:[{name:'year',mapping:function(data){return data}}],
            proxy: {
                type: 'ajax',
                url: '/categoryDictionary/getFilingyear'
            },
            autoLoad: false
        },
        margin:'5 10 0 10'
    },{
    	columnWidth:.4,
        fieldLabel:'保管期限',
        name:'entryretention',
        xtype: 'combobox',
        displayField: 'retention',
        valueField:'retention',
        queryMode: 'local',
        editable: false,
        store: {
            type:'array',
            fields:[{name:'retention',mapping:function(data){return data}}],
            proxy: {
                type: 'ajax',
                url: '/categoryDictionary/getEntryretention'
            },
            autoLoad: false
        },
        margin:'5 10 0 10'
    },{
    	columnWidth:.4,
        fieldLabel:'机构问题',
        name:'organ',
        xtype: 'combobox',
        displayField: 'item',
        valueField: 'item',
        queryMode: 'local',
        editable: false,
        store: {
            type:'array',
            fields:[{name:'item',mapping:function(data){return data}}],
            proxy: {
                type: 'ajax',
                url: '/categoryDictionary/getOrgan'
            },
            autoLoad: false
        },
        margin:'5 10 0 10'
    },{
        columnWidth:.3,
        name:'organInfo',
        editable: false,
        margin:'5 0 5 5'
    },{
    	xtype: 'button',
        width: 100,
        height: 32,
        margin: '5 0 5 5',
        name: 'resetOrgan',
        text: '重置机构'
    }]
});