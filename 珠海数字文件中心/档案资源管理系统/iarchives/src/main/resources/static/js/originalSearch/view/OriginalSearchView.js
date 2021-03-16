/**
 * Created by RonJiang on 2017/11/2 0002.
 */
Ext.define('OriginalSearch.view.OriginalSearchView', {
    extend: 'Ext.panel.Panel',
    xtype:'originalSearchView',
    layout: 'border',
    bodyBorder: false,
    items: [
        {
            itemId:'inputViewId',
            floatable: false,
            region: 'north',
            height: 105,
            layout:'column',
            items: [{
                columnWidth: .2,
                xtype : 'displayfield'
            },{
                columnWidth: .1,
                xtype : 'combo',
                itemId:'originalSearchSearchComboId',
                store : [['filename','文件名称'],
                    ['filetype','文件类型']] ,
                value: 'filename',
                margin:'40 2 0 2',
                editable:false//只能从下拉菜单中选择，不可手动编辑
            },{
                columnWidth: .3,
                xtype: 'searchfield',
                itemId:'originalSearchSearchfieldId',
                margin:'40 2 0 2',
                style: 'margin-right:2px'
            },{
                columnWidth: .09,
                xtype: 'checkboxfield',
                boxLabel: '在结果中检索',
                margin:'40 2 0 2',
                style: "margin-left:6px;margin-right:10px",
                itemId:'inresult'
            },{
                columnWidth: .31,
                xtype : 'displayfield'
            }]
        },
        {
            region: 'center',
            itemId:'gridViewId',
            xtype: 'originalSearchGridView'
        }
    ]
});