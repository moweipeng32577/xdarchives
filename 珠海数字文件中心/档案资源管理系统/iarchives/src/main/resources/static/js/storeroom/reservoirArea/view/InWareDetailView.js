/**
 * Created by tanly on 2017/12/6 0006.
 */
Ext.define('ReservoirArea.view.InWareDetailView', {
    extend:'Ext.window.Window',
    xtype: 'inWareDetailView',
    layout: 'fit',
    constructor: function(o) {
        this.baseProperty = o;
        this.callParent(arguments);
    },
    items:[{
        itemId:'detailGridView',
        xtype: 'detailGridView',
        baseProperty: this.baseProperty
    }
    ],

});


Ext.define('ReservoirArea.view.DetailGridView', {
    extend: 'Comps.view.EntryGridView',
    xtype: 'detailGridView',
    constructor: function(o) {
        //this.baseProperty = o;
        this.dataUrl='/management/findInShid/'+o.$initParent.baseProperty//查看库存详细清单
        this.callParent(arguments);
    },

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
    hasSelectAllBox:true
});