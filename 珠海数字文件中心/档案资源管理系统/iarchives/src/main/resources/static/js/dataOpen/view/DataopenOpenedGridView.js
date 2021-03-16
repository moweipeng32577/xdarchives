/**
 * Created by tanly on 2017/12/6 0006.
 */
Ext.define('Dataopen.view.DataopenOpenedGridView', {
    extend: 'Comps.view.EntryGridView',
    xtype: 'dataopenOpenedGridView',
    dataUrl: encodeURI('/dataopen/entriesByOpen?opentype=条目开放,原文开放'),
    tbar: {
        overflowHandler:'scroller',
        items: [{
	        text: '不开放',
	        iconCls:'fa fa-ban',
	        itemId: 'dontopen'
	    }, '-', {
	        text: '查看条目',
	        iconCls:'fa fa-eye',
	        itemId: 'look'
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
	    }, '-', {
			text: '发布到政务网',
			iconCls:'fa fa-check-square',
			itemId: 'release'
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