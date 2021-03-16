
Ext.define('Template.view.TemplateGridPreView',{
    extend:'Template.view.EntryGridView',
    xtype:'templateGridPreView',
    dataUrl:'/management/entries',
    tbar:[{
    //     xtype:'button',
    //     text:'设置表单字段',
    //     itemId:'editFieldBtn'
    // }, '-', {
        xtype: 'button',
        text: '设置表单界面',
        iconCls:'fa fa-columns',
        itemId: 'gridviewbtnid'
    }, '-', {
    	xtype: 'button',
    	text: '设置列表字段',
    	itemId: 'searchsort'
    }, '-', {
        xtype: 'button',
        text: '设置检索字段',
        itemId: 'listsort'
    }, "-",{
        width: 250,
        xtype:'combo',
        itemId: 'listsearchsort',
        labelWidth: 100,
        fieldLabel:'检索条件',
        store:Ext.create('Ext.data.Store', {
            proxy: {
                type: 'ajax',
                url: '/template/queryName',
                reader: {
                    type: 'json'
                }
            },
            autoLoad: true
        }),
        valueField:'item',
        displayField:'name',
        listeners: {
            //搜索条件默认选择第一项
            beforerender: function (combo) {
                var store = combo.getStore();
                store.removeAll();
                store.proxy.extraParams.xtType = window.xtType;
                store.proxy.extraParams.nodeid = window.nodeid;
                store.load();
                store.on("load", function () {
                    if (store.getCount() > 0) {
                        var record = store.getAt(0);
                        combo.select(record);
                        combo.fireEvent("select", combo, record);
                    }
                });

            }
        }
    }
    ],
    searchstore:{
        proxy: {
            type: 'ajax',
            url:'/template/queryName',
            extraParams:{nodeid:0},
            reader: {
                type: 'json',
                rootProperty: 'content',
                totalProperty: 'totalElements'
            }
        }
    },
    hasCloseButton: false,
    hasCancelButton: false,
    hasCheckColumn: false,
    hasPageBar: false,
    hasRownumber: false
});