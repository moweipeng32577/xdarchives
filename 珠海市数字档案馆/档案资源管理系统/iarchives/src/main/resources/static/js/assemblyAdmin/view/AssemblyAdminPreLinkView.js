/**
 * Created by Administrator on 2019/7/3.
 */


Ext.define('AssemblyAdmin.view.AssemblyAdminPreLinkView', {
    extend: 'Ext.panel.Panel',
    xtype: 'assemblyAdminPreLinkView',
    itemId: 'assemblyAdminPreLinkViewId',
    layout: 'border',
    items: [{
        bodyPadding: '0 100',
        region: 'north',
        layout: 'form',
        items: [{
            height: 25,
            xtype: "combobox",
            name: "assemblyflowid",
            fieldLabel: "环节",
            store: 'AssemblyFlowStore',
            editable: false,
            displayField: "modelname",
            valueField: "id",
            queryMode: "local",
            listeners: {
                afterrender: function (combo) {
                    setTimeout(function () {
                        var store = combo.getStore();
                        if (store.getCount() > 0) {
                            combo.select(store.getAt(0));
                            Ext.Ajax.request({
                                params: {
                                    id: store.proxy.extraParams.id,
                                    assemblyflowid: store.getAt(0).get('id')
                                },
                                url: '/assemblyAdmin/getAssemblyPreflowByid',
                                method: 'POST',
                                success: function (resp) {
                                    var respText = Ext.decode(resp.responseText);
                                    if (respText.success == true) {
                                        var assemblyAdminPreLinkView = combo.findParentByType('assemblyAdminPreLinkView');
                                        var linkStore = assemblyAdminPreLinkView.down('itemselector').getStore();
                                        linkStore.proxy.extraParams.id = store.proxy.extraParams.id;
                                        linkStore.proxy.extraParams.assemblyflowid = store.getAt(0).get('id');
                                        linkStore.load({
                                            callback: function () {
                                                assemblyAdminPreLinkView.down('itemselector').setValue(respText.data);
                                            }
                                        });
                                        assemblyAdminPreLinkView.assemblyflowid = store.getAt(0).get('id');
                                    }
                                },
                                failure: function () {
                                    XD.msg('操作失败');
                                }
                            });
                        }
                    }, 300);
                },
                select: function (view, record) {
                    var store = view.getStore();
                    Ext.Ajax.request({
                        params: {
                            id: store.proxy.extraParams.id,
                            assemblyflowid: record.get('id')
                        },
                        url: '/assemblyAdmin/getAssemblyPreflowByid',
                        method: 'POST',
                        success: function (resp) {
                            var respText = Ext.decode(resp.responseText);
                            if (respText.success == true) {
                                var assemblyAdminPreLinkView = view.findParentByType('assemblyAdminPreLinkView');
                                assemblyAdminPreLinkView.down('itemselector').toField.store.removeAll();
                                var linkStore = assemblyAdminPreLinkView.down('itemselector').getStore();
                                linkStore.proxy.extraParams.id = store.proxy.extraParams.id;
                                linkStore.proxy.extraParams.assemblyflowid = record.get('id');
                                linkStore.load({
                                    callback: function () {
                                        assemblyAdminPreLinkView.down('itemselector').setValue(respText.data);
                                    }
                                });
                                assemblyAdminPreLinkView.assemblyflowid = record.get('id');
                            }
                        },
                        failure: function () {
                            XD.msg('操作失败');
                        }
                    });
                }
            }
        }]

    },
        {
            bodyPadding: '0 100',
            region: 'center',
            layout:'fit',
            items: [{
                xtype: 'itemselector',
                imagePath: '../ux/images/',
                store: 'AssemblyPreflowStore',
                displayField: 'nodename',
                valueField: 'id',
                allowBlank: false,
                msgTarget: 'side',
                fromTitle: '可选环节(按Ctrl+F查找)',
                toTitle: '已选前置环节'
            }]

        }
    ],
    buttons: [
        {text: '提交', itemId: 'setPrelinkSubmit'},
        {text: '关闭', itemId: 'setPrelinkClose'}
    ]
});
