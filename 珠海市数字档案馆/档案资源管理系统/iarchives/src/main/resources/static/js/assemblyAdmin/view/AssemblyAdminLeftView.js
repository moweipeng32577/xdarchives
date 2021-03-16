/**
 * Created by Administrator on 2019/7/3.
 */


Ext.define('AssemblyAdmin.view.AssemblyAdminLeftView',{
    extend:'Ext.panel.Panel',
    xtype:'assemblyAdminLeftView',
    layout:'border',
    items:[
        {
            region: 'north',
            height:44,
            layout:'form',
            items:[
                {  xtype: "combobox",
                    name: "assemblyflowid",
                    fieldLabel: "环节",
                    store: 'AssemblyFlowStore',
                    editable: false,
                    displayField: "modelname",
                    valueField: "id",
                    queryMode: "local",
                    listeners: {
                        afterrender: function (combo) {
                            setTimeout(function(){
                                var store = combo.getStore();
                                if (store.getCount() > 0) {
                                    combo.select(store.getAt(0));
                                    Ext.Ajax.request({
                                        params: {
                                            id:store.proxy.extraParams.id,
                                            assemblyflowid:store.getAt(0).get('id')
                                        },
                                        url: '/assemblyAdmin/getAssemblyUserByid',
                                        method: 'POST',
                                        success: function (resp) {
                                            var respText = Ext.decode(resp.responseText);
                                            if (respText.success == true) {
                                                var assemblyAdminUserSetView = combo.findParentByType('assemblyAdminUserSetView');
                                                assemblyAdminUserSetView.down('itemselector').getStore().load({
                                                    callback:function(){
                                                        assemblyAdminUserSetView.down('itemselector').setValue(respText.data);
                                                    }
                                                });
                                            }
                                        },
                                        failure: function() {
                                            XD.msg('操作失败');
                                        }
                                    });
                                }
                            },300);
                        },
                        select:function (view,record) {
                            var store = view.getStore();
                            Ext.Ajax.request({
                                params: {
                                    id:store.proxy.extraParams.id,
                                    assemblyflowid:record.get('id')
                                },
                                url: '/assemblyAdmin/getAssemblyUserByid',
                                method: 'POST',
                                success: function (resp) {
                                    var respText = Ext.decode(resp.responseText);
                                    if (respText.success == true) {
                                        var assemblyAdminUserSetView = view.findParentByType('assemblyAdminUserSetView');
                                        assemblyAdminUserSetView.down('itemselector').toField.store.removeAll();
                                        assemblyAdminUserSetView.down('itemselector').getStore().load({
                                            callback:function(){
                                                assemblyAdminUserSetView.down('itemselector').setValue(respText.data);
                                            }
                                        });
                                    }
                                },
                                failure: function() {
                                    XD.msg('操作失败');
                                }
                            });
                        }
                    }
                }
            ],
            rootVisible: false,
            collapsible: true,
            hideHeaders: true,
            header: false
        },
        {
            itemId: 'organTreeViewItemId',
            region: 'center',
            header: false,
            floatable: false,
            layout: 'fit',
            items: [{xtype: 'assemblyUserSetOrganTreeView'}]
        }
    ]
});
