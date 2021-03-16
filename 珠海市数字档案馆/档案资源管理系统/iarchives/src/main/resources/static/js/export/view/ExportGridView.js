/**
 * Created by SunK on 2018/7/31 0031.
 */
Ext.define('Export.view.ExportGridView',{
    extend:'Comps.view.EntryGridView',
    xtype:'exportgrid',
    dataUrl:'/management/entries',
    region: 'north',
    height: 40,
    tbar:[
        {
            xtype: 'button',
            text : '导出EXCEL',
            itemId:'Excel',
            iconCls:'fa fa-download'
        },
        {
            xtype: 'button',
            text : '导出XML',
            itemId:'Xml',
            iconCls:'fa fa-download'
        },
        {
            xtype: 'button',
            text : '导出Excel(包括原文)',
            itemId:'ExcleAndElectronic',
            iconCls:'fa fa-download'
        },
        {
            xtype: 'button',
            text : '导出Xml(包括原文)',
            itemId:'XmlAndElectronic',
            iconCls:'fa fa-download'
        },
        {
            xtype: 'button',
            text : '导出字段模板',
            itemId:'FieldTemp',
            iconCls:'fa fa-download'
        }
    ],
    title:'当前节点：',
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