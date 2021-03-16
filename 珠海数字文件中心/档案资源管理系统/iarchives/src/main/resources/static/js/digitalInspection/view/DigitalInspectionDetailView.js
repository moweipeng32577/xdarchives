Ext.define('DigitalInspection.view.DigitalInspectionDetailView', {
    extend: 'Ext.window.Window',
    width:'100%',
    height:'100%',
    header:false,
    xtype:'DigitalInspectionDetailView',
    layout: 'border',
    bodyBorder: false,
    defaults: {
        collapsible: true,
        split: true
    },
    items: [
        {
            itemId:'detailLeftId',
            width: '23%',
            title: '(按下Ctrl+F可查找)',
            region: 'west',
            floatable: false,
            header:false,
            margin: '0 0 0 0',
            layout: 'fit',
            items: [{
                xtype:'DigitalInspectionDetailLeftView'
            }]
        },
        {
            itemId:'detailCenterId',
            collapsible: false,
            region: 'center',
            margin: '0 0 0 0',
            layout: 'fit',
            items: [{
                html:'<div id="p1" class="pw-view"></div>'
            }]
        },
        {
            itemId:'detailRightId',
            width: '27%',
            region: 'east',
            floatable: false,
            header:false,
            margin: '0 0 0 0',
            layout: 'fit',
            items: [{
                xtype:'DigitalInspectionDetailRightView'
            }]
        }
    ]
});
