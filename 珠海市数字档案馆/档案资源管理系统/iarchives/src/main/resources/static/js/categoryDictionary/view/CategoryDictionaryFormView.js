Ext.define('CategoryDictionary.view.CategoryDictionaryFormView',{
    extend:'Ext.form.Panel',
    xtype:'categoryDictionaryFormView',
    layout:'column',
    defaults:{
        layout:'form',
        xtype:'textfield',
        labelWidth: 140,
        labelSeparator:'：'
    },
    items:[{
        columnWidth:.96,
        fieldLabel:'名称',
        name:'name',
        margin:'15 1 10 15',
        allowBlank:false
    },{
        columnWidth: .02,
        xtype: 'displayfield',
        value: '<label style="color:#ff0b23;!important;">*</label>',
        margin:'15 25 1 3'
    },{
        columnWidth:.96,
        fieldLabel:'描述',
        name:'remark',
        margin:'15 1 15 15'
    }],

    buttons:[{
        text:'保存',
        itemId:'save'
    },'-',{
        text:'返回',
        itemId:'back'
    }]
});