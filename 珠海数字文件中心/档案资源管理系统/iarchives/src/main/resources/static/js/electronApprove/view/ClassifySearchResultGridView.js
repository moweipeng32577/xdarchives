/**
 * Created by Administrator on 2020/7/17.
 */

Ext.define('ElectronApprove.view.ClassifySearchResultGridView',{
    extend: 'Comps.view.EntryGridView',
    xtype:'classifySearchResultGridView',
    title: '当前位置：高级检索',
    addNodenameColumn:true,
    hasCloseButton:false,
    tbar: [{
        itemId:'highSearchLeadinId',
        xtype: 'button',
        iconCls:'fa fa-upload',
        text: '导入'
    }, '-',{
        itemId:'highSearchShowId',
        xtype: 'button',
        iconCls:'fa fa-eye',
        text: '查看'
    }, '-',{
        xtype: 'button',
        itemId:'highSearchBackId',
        iconCls:'fa fa-undo',
        text: '返回'
    }],
    dataUrl: '/classifySearch/findByClassifySearch',
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
