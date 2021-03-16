/**
 * Created by tanly on 2017/11/2 0002.
 */

Ext.define('ServiceMetadata.view.AccreditMetadataWindow', {
    extend: 'Ext.window.Window',
    xtype: 'accreditMetadataWindow',
    itemId: 'accreditMetadataWindowid',
    title: '增加参数',
    width: 750,
    height: 300,
    modal: true,
    closeToolText: '关闭',
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
    items: [{
        xtype: 'form',
        margin: '25',
        modelValidation: true,
        trackResetOnLoad: true,
        items: [{
            fieldLabel: '',
            name: 'cid',
            hidden: true
        }, {
            fieldLabel: '',
            name: 'sortsequence',
            hidden: true,
            value:'1'
        }, {
            fieldLabel: '',
            name: 'parentid',
            hidden: true
        }, {
            fieldLabel: '',
            name: 'shortname',
            itemId:'shortnameId',
            hidden: true
        },{
            xtype : 'combo',
            itemId:'operationComboId',
            store :'ServiceMetadataOperationStore',
            displayField: 'code',
            name: 'operation',
            valueField: 'code',
            fieldLabel: '业务行为',
            style: 'margin-right:2px',
            allowBlank: false,
            editable:false,//只能从下拉菜单中选择，不可手动编辑
        }, {
            xtype: 'textfield',
            fieldLabel: '业务状态',
            allowBlank: false,
            name: 'mstatus'
        }, {
            xtype: 'textfield',
            fieldLabel: '行为描述',
            allowBlank: false,
            name: 'operationmsg'
        }, {
            xtype : 'combo',
            itemId:'shortnameComboId',
            store :'ServiceMetadataAccreditStore',
            displayField: 'shortname',
            name: 'aid',
            valueField: 'aid',
            fieldLabel: '授权标识',
            style: 'margin-right:2px',
            allowBlank: false,
            editable:false,//只能从下拉菜单中选择，不可手动编辑
            listeners: {
                select:function(combo,records){
                    var accreditMetadataWindow = combo.findParentByType('accreditMetadataWindow');
                    var shortnameId = accreditMetadataWindow.down('[itemId=shortnameId]');
                    shortnameId.setValue(records.data.shortname);

                },
                render:function (combo) {
                    var accreditMetadataWindow = combo.findParentByType('accreditMetadataWindow');
                    var shortnameId = accreditMetadataWindow.down('[itemId=shortnameId]');
                    shortnameId.setValue(combo.getValue());
                }
            }
        }]
    }]
    ,
    buttons: [{
        text: '保存',
        itemId: 'save'
    }, {
        text: '取消',
        itemId: 'cancel'
    }
    ]
});
