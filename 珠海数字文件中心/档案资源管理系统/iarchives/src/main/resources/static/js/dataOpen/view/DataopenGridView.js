/**
 * Created by tanly on 2017/12/1 0001.
 */
Ext.define('Dataopen.view.DataopenGridView', {
    extend: 'Comps.view.EntryGridView',
    xtype: 'dataopengrid',
    dataUrl: encodeURI('/dataopen/entriesByOpen?opentype'),
    tbar: {
        overflowHandler:'scroller',
        items: [{
	    	text: '直接开放',
	        iconCls:'fa fa-plus',
	        itemId: 'open'
	    }, '-', {
	        text: '加入送审单',
	        iconCls:'fa fa-plus',
	        itemId: 'add'
	    }, '-', {
	        text: '处理送审单',
	        iconCls:'fa fa-pencil-square-o',
	        itemId: 'deal'
	    }, '-', {
	        text: '查看条目',
	        iconCls:'fa fa-eye',
	        itemId: 'look'
	    }, '-', {
	        text: '查看开放单据',
	        iconCls:'fa fa-file-o',
	        itemId: 'showopendoc'
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