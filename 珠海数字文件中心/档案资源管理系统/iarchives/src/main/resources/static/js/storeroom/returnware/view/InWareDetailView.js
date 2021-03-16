/**
 * Created by tanly on 2017/12/6 0006.
 */
Ext.define('ReturnWare.view.InWareDetailView', {
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


Ext.define('Inware.view.DetailGridView', {
    extend: 'Comps.view.EntryGridView',
    xtype: 'detailGridView',
    constructor: function(o) {
        //this.baseProperty = o;
        this.dataUrl='/management/findOne/'+o.$initParent.baseProperty//查看入库详细清单
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