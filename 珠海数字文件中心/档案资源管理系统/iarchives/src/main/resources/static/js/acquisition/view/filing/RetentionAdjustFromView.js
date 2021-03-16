/**
 * Created by RonJiang on 2018/5/22 0022.
 */
Ext.define('Acquisition.view.filing.RetentionAdjustFromView',{
    extend:'Ext.form.Panel',
    width: 280,
    height: 120,
    layout: {
        type: 'vbox',
        align: 'center'
    },
    xtype:'retentionAdjustFromView',
    items:[{
        xtype: 'combobox',
        fieldLabel: '保管期限',
        labelWidth: 120,
        width: 220,
        height:30,
        name:'entryretention',
        queryMode: 'local',
        forceSelection: true,
        displayField: 'code',
        valueField: 'code',
        editable: false,
        disabled: false,
        margin:'15 0 0 0',

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
