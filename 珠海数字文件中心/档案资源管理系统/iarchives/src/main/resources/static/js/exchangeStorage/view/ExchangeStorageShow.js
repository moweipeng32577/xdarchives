/**
 * Created by yl on 2018/3/19.
 */
Ext.define('ExchangeStorage.view.ExchangeStorageShow', {
    extend: 'Ext.Panel',
    xtype: 'exchangeStorageShow',
    requires: [
        'Ext.layout.container.Border'
    ],
    region: 'south',
    layout: 'border',
    height: 270,
    minHeight: 270,
    split: true,
    items: [{
        region: 'west',
        width: '15%',
        xtype: 'treepanel',
        itemId: 'treepanelId',
        autoScroll: true,
        containerScroll: true,
        split: 1,
        header: false,
        hideHeaders: true,
        rootVisible:false,
        store: {
            model:'ExchangeStorage.model.ExchangeStorageTreeModel',
            proxy: {
                type: 'ajax',
                url: '/exchangeStorage/showExchangeStorage',
                extraParams:{pcid:''},
                reader: {
                    type: 'json',
                    expanded: true
                }
            },
            root: {
                text: '文件名称'
            },
            listeners:{
                nodebeforeexpand:function(node, deep, animal) {
                    if((node.raw)){
                        this.proxy.extraParams.pcid = node.raw.fnid;
                    }
                }
            }
        }
    }, {
        xtype:'panel',
        region: 'center',
        split: true,
        layout:'fit',
        requires: [
            'Ext.data.*',
            'Ext.util.*',
            'Ext.view.View',
            'Ext.ux.DataView.Animated'
        ],
        items:{
            xtype: 'dataview',
            itemId:'dataview',
            reference: 'dataview',
            trackOver: true,
            overItemCls: 'x-item-over',
            draggable:false,

            plugins: [
                {
                    ptype: 'ux-animated-dataview'
                },
                Ext.create('Ext.ux.DataView.DragSelector', {}),
                Ext.create('Ext.ux.DataView.LabelEditor', {dataIndex: 'name'})
            ],

            store:{
                fields: ['name', 'url'],
                proxy: {
                    type: 'ajax',
                    url: '/exchangeStorage/getDataView',
                    extraParams: {
                        parentid:''
                    },
                    reader: {
                        type: 'json',
                        rootProperty: 'content'
                    }
                }
            },

            prepareData: function (data) {
                Ext.apply(data, {
                    shortName: Ext.util.Format.ellipsis(data.name, 15),
                    sizeString: Ext.util.Format.fileSize(data.size),
                    dateString: Ext.util.Format.date(data.lastmod, "m/d/Y g:i a")
                });
                return data;
            },
            selectionModel: {
                mode: 'MULTI'
            },
            itemSelector: 'div.thumb-wrap',

            tpl: [
                '<tpl for=".">',
                '<div class="thumb-wrap" style="float:left; margin:5px;" id="{name}" >',
                '<center><div class="thumb"><img width="50px" height="50px" src="{url}" /></div>',
                '<span>{shortName}</span></center>',
                '</div>',
                '</tpl>',
                '<div class="x-clear"></div>'
            ],

            listeners: {
                itemclick: function (view, item) {
                    // var name = item.data.name;
                    // alert(name);
                },
                selectionchange: function (dv, nodes) {
                    // var l = nodes.length,
                    //     s = l !== 1 ? 's' : '';
                    // //this.up('panel').down('fieldcontainer').getComponent('selectItem').setText('已选择 (' + l + ')个');
                    // this.up('MediaListSort').selectedItem = nodes;
                    // alert(dv);
                },
                itemdblclick: function (view, item) {
                    var fileName = item.data.name;
                    window.open("/exchangeStorage/openSipFile?fileName=" + fileName);
                },
                itemmouseenter : function (view, index, target, record, e, eOpts ) {
                    if (view.tip == null) {
                        view.tip = Ext.create('Ext.tip.ToolTip', {
                            target: view.el,
                            delegate: view.itemSelector,
                            renderTo: Ext.getBody()

                        });
                    };
                    view.el.clean();
                    view.tip.update('双击可以打开文件');
                }
}
        }
    }, {
        xtype: 'exchangeStorageDetailGridView'
    }]
});