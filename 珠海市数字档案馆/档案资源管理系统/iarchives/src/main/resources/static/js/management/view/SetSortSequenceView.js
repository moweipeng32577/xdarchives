/**
 * Created by Administrator on 2020/5/12.
 */


var multiSelectSetStore = Ext.create("Ext.data.Store", {
    fields: ["text", "value"]
});

var multiSelectGetStore = Ext.create("Ext.data.Store", {
    fields: ["value"]
});

Ext.define('Management.view.SetSortSequenceView', {
    extend: 'Ext.window.Window',
    xtype: 'setSortSequenceView',
    itemId: 'setSortSequenceViewId',
    title: '排序设置',
    resizable: false,
    width: 685,
    height: 550,
    bodyPadding: '20',
    layout: 'fit',
    modal: true,
    closeToolText: '关闭',
    items: [{
        layout: 'border',
        items: [{
            region: 'west',
            xtype: 'panel',
            title: '排序对象',
            width: 250,
            border: 1,
            items: [{
                xtype: 'multiselect',
                itemId: 'multiItemGetId',
                width: 250,
                height: 450,
                valueField: 'value',
                displayField: 'value',
                store: multiSelectGetStore,
                border: 1,
                getSelections: function (list) {
                    var store = list.getStore();

                    return Ext.Array.sort(list.getSelectionModel().getSelection(), function (a, b) {
                        a = store.indexOf(a);
                        b = store.indexOf(b);

                        if (a < b) {
                            return -1;
                        } else if (a > b) {
                            return 1;
                        }
                        return 0;
                    });
                }
            }]
        }, {
            width: 130,
            height: 500,
            xtype: 'panel',
            layout: 'column',
            items: [{
                columnWidth: 1,
                xtype: 'combo',
                itemId: 'comboItem',
                width: '81%',
                store: [
                    ['升序', '升序'],
                    ['降序', '降序']
                ],
                value: '',
                margin: '100 10 0 10',
                editable: false,
                listeners: {
                    afterrender: function (combo) {
                        var store = combo.getStore();
                        if (store.getCount() > 0) {
                            combo.select(store.getAt(0));
                        }
                    }
                }
            }, {
                columnWidth: 1,
                xtype: 'button',
                // text: '顶',
                itemId: 'submitBtn1',
                iconCls: 'fa fa-arrow-up',
                margin: '10 40 0 40',
                handler: function (view) {
                    var setSortSequenceView = view.findParentByType('setSortSequenceView');
                    var multiItemSet = setSortSequenceView.down('[itemId=multiItemSetId]');
                    var selected = multiItemSet.getSelections(multiItemSet.boundList);
                    var store = multiItemSet.getStore();
                    store.suspendEvents();
                    store.remove(selected, true);
                    store.insert(0, selected);
                    store.resumeEvents();
                    multiItemSet.boundList.refresh();
                    multiItemSet.boundList.getSelectionModel().select(selected);
                }
            }, {
                columnWidth: 1,
                xtype: 'button',
                // text: '↑',
                itemId: 'submitBtn2',
                iconCls: 'fa fa-long-arrow-up',
                margin: '10 40 0 40',
                handler: function (view) {
                    var setSortSequenceView = view.findParentByType('setSortSequenceView');
                    var multiItemSet = setSortSequenceView.down('[itemId=multiItemSetId]');
                    var selected = multiItemSet.getSelections(multiItemSet.boundList);
                    var store = multiItemSet.getStore();
                    var rec;
                    var i = 0;
                    var len = selected.length;
                    var index = 0;
                    store.suspendEvents();
                    for (; i < len; ++i, index++) {
                        rec = selected[i];
                        index = Math.max(index, store.indexOf(rec) - 1);
                        store.remove(rec, true);
                        store.insert(index, rec);
                    }
                    store.resumeEvents();
                    multiItemSet.boundList.refresh();
                    multiItemSet.boundList.getSelectionModel().select(selected);
                }
            }, {
                columnWidth: 1,
                xtype: 'button',
                // text: '→',
                itemId: 'submitBtn3',
                iconCls: 'fa fa-long-arrow-right',
                margin: '10 40 0 40',
                handler: function (view) {
                    var setSortSequenceView = view.findParentByType('setSortSequenceView');
                    var multiItemGet = setSortSequenceView.down('[itemId=multiItemGetId]');
                    var multiItemSet = setSortSequenceView.down('[itemId=multiItemSetId]');
                    var muV = multiItemGet.getSelections(multiItemGet.boundList);
                    var getStore = multiItemGet.getStore();
                    var setStore = multiItemSet.getStore();
                    var sorttype = setSortSequenceView.down('[itemId=comboItem]').getValue();
                    for (var i = 0; i < muV.length; i++) {
                        var itemObj = new Object();
                        itemObj.text = muV[i].data.value + '_' + sorttype;
                        itemObj.value = muV[i].data.value;
                        var record = new Ext.data.Record(itemObj);
                        setStore.add(record);
                    }
                    getStore.suspendEvents();
                    getStore.remove(muV, true);
                    getStore.resumeEvents();
                    multiItemGet.boundList.refresh();
                }
            }, {
                columnWidth: 1,
                xtype: 'button',
                // text: '←',
                itemId: 'submitBtn4',
                iconCls: 'fa fa-long-arrow-left',
                margin: '10 40 0 40',
                handler: function (view) {
                    var setSortSequenceView = view.findParentByType('setSortSequenceView');
                    var multiItemGet = setSortSequenceView.down('[itemId=multiItemGetId]');
                    var multiItemSet = setSortSequenceView.down('[itemId=multiItemSetId]');
                    var selected = multiItemSet.getSelections(multiItemSet.boundList);
                    var getStore = multiItemGet.getStore();
                    var setStore = multiItemSet.getStore();
                    setStore.suspendEvents();
                    setStore.remove(selected, true);
                    setStore.resumeEvents();
                    Ext.Ajax.request({
                        method: 'post',
                        url: '/summarization/getSelectedByNodeId',
                        params: {
                            nodeid: setSortSequenceView.nodeid
                        },
                        success: function (response) {
                            var data = Ext.decode(response.responseText);
                            getStore.removeAll();
                            for (var i = 0; i < data.length; i++) {
                                var flag = true;
                                for (var j = 0; j < setStore.getCount(); j++) {
                                    if (data[i].fieldname == setStore.getAt(j).get('value')) {
                                        flag = false;
                                        break;
                                    }
                                }
                                if (flag) {
                                    var itemObj = new Object();
                                    itemObj.value = data[i].fieldname;
                                    var record = new Ext.data.Record(itemObj);
                                    getStore.add(record);
                                }
                            }
                        }
                    });
                    multiItemSet.boundList.refresh();
                }
            }, {
                columnWidth: 1,
                xtype: 'button',
                // text: '↓',
                iconCls: 'fa fa-long-arrow-down',
                itemId: 'submitBtn5',
                margin: '10 40 0 40',
                handler: function (view) {
                    var setSortSequenceView = view.findParentByType('setSortSequenceView');
                    var multiItemSet = setSortSequenceView.down('[itemId=multiItemSetId]');
                    var selected = multiItemSet.getSelections(multiItemSet.boundList);
                    var rec;
                    var store = multiItemSet.getStore();
                    var i = selected.length - 1;
                    var index = store.getCount() - 1;
                    store.suspendEvents();
                    for (; i > -1; --i, index--) {
                        rec = selected[i];
                        index = Math.min(index, store.indexOf(rec) + 1);
                        store.remove(rec, true);
                        store.insert(index, rec);
                    }
                    store.resumeEvents();
                    multiItemSet.boundList.refresh();
                    multiItemSet.boundList.getSelectionModel().select(selected);
                }
            }, {
                columnWidth: 1,
                xtype: 'button',
                // text: '底',
                itemId: 'submitBtn6',
                iconCls: 'fa fa-arrow-down',
                margin: '10 40 0 40',
                handler: function (view) {
                    var setSortSequenceView = view.findParentByType('setSortSequenceView');
                    var multiItemSet = setSortSequenceView.down('[itemId=multiItemSetId]');
                    var selected = multiItemSet.getSelections(multiItemSet.boundList);
                    var store = multiItemSet.getStore();
                    store.suspendEvents();
                    store.remove(selected, true);
                    store.add(selected);
                    store.resumeEvents();
                    multiItemSet.boundList.refresh();
                    multiItemSet.boundList.getSelectionModel().select(selected);
                }
            }]
        }, {
            region: 'east',
            xtype: 'panel',
            title: '排序结果',
            width: 250,
            border: 1,
            layout: 'border',
            items: [{
                region: 'center',
                xtype: 'multiselect',
                itemId: 'multiItemSetId',
                valueField: 'value',
                displayField: 'text',
                store: multiSelectSetStore,
                border: false,
                width: 250,
                height: 450,
                readOnly: true,
                getSelections: function (list) {
                    var store = list.getStore();

                    return Ext.Array.sort(list.getSelectionModel().getSelection(), function (a, b) {
                        a = store.indexOf(a);
                        b = store.indexOf(b);

                        if (a < b) {
                            return -1;
                        } else if (a > b) {
                            return 1;
                        }
                        return 0;
                    });
                }
            }, {
                region: 'south',
                xtype: 'button',
                itemId: 'resetBtn',
                text: '清空',
                handler: function (view) {
                    var setSortSequenceView = view.findParentByType('setSortSequenceView');
                    var multiItemSet = setSortSequenceView.down('[itemId=multiItemSetId]');
                    var multiItemGet = setSortSequenceView.down('[itemId=multiItemGetId]');
                    var getstore = multiItemGet.getStore();
                    var store = multiItemSet.getStore();
                    store.suspendEvents();
                    store.removeAll();
                    store.resumeEvents();
                    Ext.Ajax.request({
                        method: 'post',
                        url: '/summarization/getSelectedByNodeId',
                        params: {
                            nodeid: setSortSequenceView.nodeid
                        },
                        success: function (response) {
                            var data = Ext.decode(response.responseText);
                            getstore.removeAll();
                            for (var i = 0; i < data.length; i++) {
                                var itemObj = new Object();
                                itemObj.value = data[i].fieldname;
                                var record = new Ext.data.Record(itemObj);
                                getstore.add(record);
                            }
                        }
                    });
                    multiItemSet.boundList.refresh();
                }
            }]
        }]
    }],
    listeners:{
        close:function (view) {
            view.down('[itemId=multiItemSetId]').getStore().removeAll();
            view.down('[itemId=multiItemGetId]').getStore().removeAll();
        }
    },
    buttons: [
        {text: '提交', itemId: 'sortSubmit'},
        {text: '关闭', itemId: 'sortClose'}
    ]
});