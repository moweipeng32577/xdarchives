/**
 * Created by Rong on 2019-01-17.
 */
Ext.define('Lot.view.JK.JKFrameView',{
    extend:'Ext.panel.Panel',
    xtype:'jkframe',
    bodyStyle : 'overflow-x:scroll; overflow-y:hidden',
    autoScroll : true,
    layout:'column',
    tbar: [{
        xtype: "container",
        items: [{
            xtype: 'toolbar',
            dock: 'top',
            items: [{
                itemId:'comboPanel',
                xtype:'combo',
                store: new Ext.data.ArrayStore({
                    fields: ['text', 'value'],
                    data: [['画面布局：1', 'p1'], ['画面布局：2', 'p2'],['画面布局：3', 'p3'], ['画面布局：4', 'p4']]
                }),
                valueField: 'value',
                displayField: 'text',
                value:'p1'
            }]
        }]
    }],
    choosepanel : function(panel) {
        window.jkPanel = panel;
        var panels = this.items;
        for (var i = 0; i < panels.length; i++) {
            if (panels.get(i) == panel) {
                panels.get(i).el.dom.style.setProperty('border-color', 'red', 'important')
            } else {
                panels.get(i).el.dom.style.setProperty('border-color', '#5fa2dd', 'important')
            }
        }
    },
});