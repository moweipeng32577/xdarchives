/**
 * Created by RonJiang on 2018/01/24
 */
Ext.define('Summarization.view.SummarizationView', {
    extend: 'Ext.panel.Panel',
    xtype: 'summarization',
    layout: 'card',
    items: [{
        itemId: 'formview',
        layout: 'border',
        items: [{
            region: 'west',
            width: XD.treeWidth,
            xtype: 'treepanel',
            itemId: 'summarizationTreeId',
            rootVisible: false,
            store: 'SummarizationTreeStore',
            collapsible: true,
            split: 1,
            header: false,
            hideHeaders: true
        }, {
            region: 'center',
            layout: 'fit',
            xtype: 'summarizationPromptView'
        }]
    }]
});