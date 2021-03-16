/**
 * Created by tanly on 2017/11/17 0002.
 */
Ext.define('FullSearch.view.FullSearchView', {
    extend: 'Ext.panel.Panel',
    xtype:'fullSearchView',
    layout: 'border',
    bodyBorder: false,
    items: [
        {
            itemId:'inputViewId',
            floatable: false,
            region: 'north',
            height: 105,
            margin: '0 0 0 0',
            layout: {
                type: 'hbox',
                align: 'middle',
                pack: 'center'
            },
            items: [{
                width: 500,
                xtype: 'searchfield',
                itemId:'fullSearchSearchfieldId',
                style: "margin-right:2px",
                emptyText: '请输入关键词',
                allowBlank: false,
                blankText: '请输入关键词'
            },{
                xtype:'checkbox',
                boxLabel:'在结果中检索',
                margin:'15',
                itemId:'inresult'
            }]
        },
        {
            itemId:'gridViewId',
            hidden: false,
            collapsible: false,
            region: 'center',
            margin: '0 0 0 0',
            layout: 'fit',
            items: [{xtype: 'fullSearchGridView'}]
        }
    ]
});