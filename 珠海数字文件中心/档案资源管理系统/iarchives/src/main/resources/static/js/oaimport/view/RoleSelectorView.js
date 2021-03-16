/**
 * Created by tanly on 2018/7/25 0025.
 */
Ext.define('OAImport.view.RoleSelectorView', {
    extend: 'Ext.window.Window',
    xtype: 'roleSelectorView',
    itemId:'RoleSelectorViewId',
    title: '授权角色',
    width: 550,
    height: 220,
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
            fieldLabel: '',
            name:'classid',
            hidden:true,
            itemId:'classiditemid'
        },{
            xtype: 'combobox',
            fieldLabel: '角色名称',
            editable:false,
            allowBlank: false,
            displayField: 'rolename',
            valueField: 'roleid',
            store: Ext.create('Ext.data.Store', {
                proxy: {
                    type: 'ajax',
                    url: '/oaimport/getAuthorizedRole',
                    reader: {
                        type: 'json'
                    }
                },
                autoLoad: true
            }),
            name: 'role',
            itemId: 'rolenameItem',
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