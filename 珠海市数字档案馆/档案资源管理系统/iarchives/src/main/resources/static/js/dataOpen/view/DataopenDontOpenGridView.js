/**
 * Created by tanly on 2017/12/7 0007.
 */
Ext.define('Dataopen.view.DataopenDontOpenGridView', {
    extend: 'Comps.view.EntryGridView',
    xtype: 'dataopenDontOpenGridView',
    dataUrl: encodeURI('/dataopen/entriesByOpen?opentype=不开放'),
    tbar: {
        overflowHandler:'scroller',
        items: [{
            text: '撤销不开放',
            iconCls: 'fa fa-minus-square-o',
            itemId: 'cancelban'
        }, '-', {
            text: '查看条目',
            itemId: 'look',
            iconCls: 'fa fa-eye'
        }, '-', {
	        iconCls:'',
	        itemId:"exportID",
	        menu:[{
	            text : '导出EXCEL',
	            itemId:'Excel',
	            iconCls:'fa fa-download'
	        }, '-', {
	            text : '导出XML',
	            itemId:'Xml',
	            iconCls:'fa fa-download'
	        }, '-', {
	            text : '导出Excel(包括原文)',
	            itemId:'ExcleAndElectronic',
	            iconCls:'fa fa-download'
	        }, '-', {
	            text : '导出Xml(包括原文)',
	            itemId:'XmlAndElectronic',
	            iconCls:'fa fa-download'
	        }],
	        text:'导出'
	    }]
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
    }
});