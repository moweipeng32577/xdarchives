/**
 * Created by SunK on 2020/6/8 0008.
 */
Ext.define('MetadataManagement.view.AddMetadataView', {
    extend: 'Ext.window.Window',
    xtype: 'accreditMetadataWindow',
    itemId: 'accreditMetadataWindowid',
    title: '增加参数',
    width: 750,
    height: 360,
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
            name: 'sid',
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
        }, {
            fieldLabel: '',
            name: 'realname',
            itemId:'realnameId',
            hidden: true
        },{
            xtype : 'combo',
            itemId:'operationComboId',
            store :'AddMetadataOperationStore',
            displayField: 'code',
            name: 'operation',
            valueField: 'code',
            fieldLabel: '业务行为',
            style: 'margin-right:2px',
            allowBlank: false,
            editable:false,//只能从下拉菜单中选择，不可手动编辑
            listeners: {
                select:function(combo,records){
                    var accreditMetadataWindow = combo.findParentByType('accreditMetadataWindow');
                    var shortnameId = accreditMetadataWindow.down('[itemId=shortnameComboId]');
                    var realnameComboId = accreditMetadataWindow.down('[itemId=realnameComboId]');
                    var form = this.up('form');
                    form.load({
                        url: '/metadataManagement/getAddServiceMetadata',
                        method: 'POST',
                        params: {
                            operation: this.getValue()
                        },
                        success: function (form, action) {
                            var respText = Ext.decode(action.response.responseText);
                            if (!respText.success) {
                                XD.msg('获取表单信息失败');
                                return;
                            }
                            shortnameId.getStore().reload();
                            var realnameCombStore = realnameComboId.getStore();
                            realnameCombStore.proxy.extraParams.userid = respText.data.userid;
                            realnameCombStore.reload();
                        }
                    });
                }
            }
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
            store :'AddMetadataAccreditStore',
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
        }, {
            xtype : 'combo',
            itemId:'realnameComboId',
            store :'AddmetadataUserStore',
            name: 'userid',
            displayField : 'realname',
            valueField : 'userid',
            fieldLabel: '操作人员',
            style: 'margin-right:2px',
            allowBlank: false,
            editable:false,//只能从下拉菜单中选择，不可手动编辑
            listeners: {
                // select:function(combo,records){
                //     var accreditMetadataWindow = combo.findParentByType('accreditMetadataWindow');
                //     var realnameId = accreditMetadataWindow.down('[itemId=realnameId]');
                //     realnameId.setValue(records.data.realname);
                //
                // },
                // render:function (combo) {
                //     var accreditMetadataWindow = combo.findParentByType('accreditMetadataWindow');
                //     var realnameId = accreditMetadataWindow.down('[itemId=realnameId]');
                //     realnameId.setValue(combo.getValue());
                // }
            }
        },{
            // xtype: 'textfield',
            // fieldLabel: '时间',
            // allowBlank: false,
            // name: 'servicetime'
            xtype: 'datefield',
            fieldLabel: '开始时间',
            name: 'servicetime',
            allowBlank: false,
            format: 'Y-m-d H:i:s'
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