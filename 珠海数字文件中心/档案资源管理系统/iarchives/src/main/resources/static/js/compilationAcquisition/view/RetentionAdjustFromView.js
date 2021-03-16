/**
 * Created by RonJiang on 2018/5/22 0022.
 */
Ext.define('CompilationAcquisition.view.RetentionAdjustFromView',{
    extend:'Ext.form.Panel',
    layout:'fit',
    xtype:'retentionAdjustFromView',
    items:[{
        xtype: 'combobox',
        fieldLabel: '保管期限',
        height:10,
        name:'entryretention',
        queryMode: 'local',
        forceSelection: true,
        displayField: 'code',
        valueField: 'code',
        editable: false,
        disabled: false,
        margin:'5 5 5 5',
        store: Ext.create('Ext.data.Store',{
            proxy: {
                type: 'ajax',
                extraParams:{
                    value:'Retention'
                },
                url: '/systemconfig/enums',
                reader: {
                    type: 'json'
                }
            },
            autoLoad: false
        }),
        listeners:{
            afterrender:function(combo){
                var store = combo.getStore();
                store.load(function(){
                    if(this.getCount() > 0){
                        combo.select(this.getAt(0));
                    }
                });
            }
        }
    }],
    buttons:[{
        text:'确定',
        itemId:'retentionAjustConfirm'
    },'-',{
        text:'返回',
        itemId:'retentionAjustBack'
    }]
});