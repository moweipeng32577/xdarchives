/**
 * Created by yl on 2017/10/26.
 */
Ext.define('Appraisal.view.AppraisalGridView', {
    extend:'Comps.view.EntryGridView',
    xtype: 'appraisalGridView',
    dataUrl:'/appraisal/getEntryIndex',
    title: '当前位置：',
    region: 'center',
    tbar: [{
        xtype: 'button',
        text: '查看',
        itemId:'look',
        iconCls:'fa fa-eye'
    }, '-', {
        xtype: 'button',
        text: '新增销毁单据',
        iconCls:'fa fa-plus-circle',
        itemId:'submitAppraisalID'
    }/*, '-', {
        xtype: 'button',
        itemId:'appraisalBtnID',
        iconCls:'fa fa-balance-scale',
        text: '鉴定'
    }*/, '-', {
        xtype: 'button',
        itemId:'printAppraisalID',
        iconCls:'fa fa-print',
        text: '打印'
    }, '-', {
        xtype: 'button',
        itemId:'showBillID',
        iconCls:'fa fa-newspaper-o',
        text: '查看销毁单据'
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
        xtype: 'button',
        itemId:'updateTree',
        text: '更新到期鉴定节点树'
    }],
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