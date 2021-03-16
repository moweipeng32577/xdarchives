/**
 * Created by tanly on 2018/8/9 0025.
 */
Ext.define('OAImport.view.NodeSelectorView', {
    extend: 'Ext.window.Window',
    xtype: 'nodeSelectorView',
    itemId:'NodeSelectorViewId',
    title: '授权工作流节点',
    width: 550,
    height: 250,
    modal:true,
    closeToolText:'关闭',
    layout: {
        type: 'vbox',
        align: 'stretch'
    },

    defaults: {
        layout: 'form',
        xtype: 'container',
        defaultType: 'textfield',
        style: 'width: 50%'
    },
    items:[{
        xtype: 'form',
        margin:'25',
        modelValidation: true,
        trackResetOnLoad:true,
        items: [{
            xtype: 'combobox',
            fieldLabel: '工作流名称',
            editable:false,
            allowBlank: false,
            displayField: 'text',
            valueField: 'id',
            store: Ext.create('Ext.data.Store', {
                proxy: {
                    type: 'ajax',
                    url: '/oaimport/getWork',
                    reader: {
                        type: 'json'
                    }
                },
                autoLoad: true
            }),
            name: 'node',
            itemId: 'workItem',
            afterLabelTextTpl: [
                '<span style="color:red;font-weight:bold" data-qtip="必填项">*</span>'
            ],
            listeners: {
                select:function (cmp,data) {
                    cmp.up('nodeSelectorView').down('[itemId=nodeItem]').clearValue();
                    cmp.up('nodeSelectorView').down('[itemId=nodeItem]').store.load({
                        params:{
                            workid:data.get('id')
                        }
                    })
                }
            }
        },{
            xtype: 'combobox',
            fieldLabel: '节点名称',
            allowBlank: false,
            displayField: 'text',
            valueField: 'id',
            editable: false,
            queryMode: "local",
            store: Ext.create('Ext.data.Store', {
                proxy: {
                    type: 'ajax',
                    url: '/oaimport/getNode',
                    reader: {
                        type: 'json'
                    }
                },
                autoLoad: true
            }),
            name: 'node',
            itemId: 'nodeItem',
            afterLabelTextTpl: [
                '<span style="color:red;font-weight:bold" data-qtip="必填项">*</span>'
            ],
            listeners: {
                afterrender: function (combo) {
                    var store = combo.getStore();
                    store.load(function () {
                        if (this.getCount() > 0) {
                            combo.select(this.getAt(0));
                        }
                    });
                }
            }
        },{
            xtype: "checkbox",
            name: 'include',
            inputValue: "true",
            itemId:'includeItem',
            fieldLabel: '包含下属机构',
            labelWidth:140
        }
        ]
    }]
    ,
    buttons: [{
        text: '授权',
        itemId:'submit'
    },{
        text: '取消',
        itemId:'cancel'
    }
    ]
});