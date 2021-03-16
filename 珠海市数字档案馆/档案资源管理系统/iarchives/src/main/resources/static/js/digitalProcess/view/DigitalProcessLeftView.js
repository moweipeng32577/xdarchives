/**
 * 表格与表单视图
 */
// var AssemblyStore = Ext.create("Ext.data.Store", {
//     fields: ["title", "code"],
//     data: [
//         { title: "数字化加工流程", code: '2018112609292881' }
//     ]
// });

Ext.define('DigitalProcess.view.DigitalProcessLeftView',{
    extend:'Ext.panel.Panel',
    xtype:'DigitalProcessLeftView',
    layout:'border',
    items:[
        {
            region: 'north',
            height:140,
            layout:'form',
            items:[
                {  xtype: "combobox",
                    name: "assemblycode",
                    fieldLabel: "流水线",
                    store: 'AssemblyStore',
                    editable: false,
                    displayField: "title",
                    valueField: "code",
                    queryMode: "local",
                    itemId:'assemblyBoxId',
                    listeners: {
                        afterrender: function (combo) {
                            combo.getStore().on('load',function(){
                                var store = combo.getStore();
                                if (store.getCount() > 0) {
                                    var record = store.getAt(0);
                                    combo.select(record);
                                    combo.fireEvent('select', combo, record);
                                    var treestore = combo.findParentByType('DigitalProcessLeftView').down('[itemId=treepanelId]').getStore();
                                    treestore.proxy.extraParams.assemblyid = store.getAt(0).get('id');
                                    treestore.reload();
                                    combo.assemblyid = store.getAt(0).get('id');
                                }
                            });
                        },
                        select:function (view,record) {
                            var treestore = view.findParentByType('DigitalProcessLeftView').down('[itemId=treepanelId]').getStore();
                            treestore.proxy.extraParams.assemblyid = record.get('id');
                            treestore.reload();
                            view.assemblyid = record.get('id');
                        }
                    }
                },
                {
                    xtype: "combobox",
                    itemId: "treepanelId",
                    fieldLabel: "环节",
                    store: 'DigitalProcessTreeStore',
                    editable: false,
                    queryMode: "local",
                    listeners:{
                        beforerender: function (combo) {
                            combo.getStore().on('load',function(){
                                var store = combo.getStore();
                                if (store.getCount() > 0) {
                                    var record = store.getAt(0);
                                    combo.select(record);
                                    combo.fireEvent('select', combo, record);
                                    var DigitalProcessTabView = combo.findParentByType('DigitalProcessLeftView').up('DigitalProcessView').down('DigitalProcessTabView');
                                    DigitalProcessTabView.firstloadtype = 'firstload';
                                } else{
                                    //当所切换的流水线是空环节时，清空上一流水线的环节及批次搜索框
                                    combo.findParentByType('DigitalProcessLeftView').down('[itemId=treepanelId]').setValue("");
                                    combo.findParentByType('DigitalProcessLeftView').down('[itemId=batchcodeNameSearch]').setValue("");
                                }
                            });
                        }
                    }
                },
                {
                    xtype: 'searchfield',
                    itemId: 'batchcodeNameSearch',
                    emptyText:'请输入批次名'
                }
            ],
            rootVisible: false,
            collapsible: true,
            split: 1,
            hideHeaders: true,
            header: false
        },
        {
            region: 'center',
            xtype: 'treepanel',
            itemId: 'treepanelCalloutId',
            rootVisible: false,
            store: 'DigitalProcessCalloutTreeStore',
            collapsible: true,
            split: 1,
            hideHeaders: true,
            title:''
            // header: false
        }
        ]
});