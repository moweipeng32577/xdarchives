/**
 * Created by RonJiang on 2018/01/24
 */
Ext.define('CodesettingAlign.view.CodesettingAlignResultGridView',{
    extend: 'Comps.view.EntryGridView',
    xtype:'codesettingAlignResultGridView',
    title: '当前位置：',
    hasCheckColumn:true,        //选择框
    hasCloseButton:false,        //关闭按钮
    hasCancelButton:false,       //取消选择按钮
    tbar: [{
        itemId:'codesettingAlignBackId',
        xtype: 'button',
        iconCls:'fa fa-arrow-left',
        text: '返回'
    },'-',{
        itemId:'codesettingAlignShowId',
        xtype: 'button',
        iconCls:'fa fa-eye',
        text: '查看'
    }, '-', {
        itemId:'codesettingAlignSettingId',
        xtype: 'button',
        iconCls:'fa fa-cog',
        text: '档号设置'
    }, '-', {
        itemId:'codesettingAlignAlignId',
        xtype: 'button',
        iconCls:'fa fa-align-justify',
        text: '档号对齐'
    }],
    dataUrl:'/classifySearch/findBySearch',
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